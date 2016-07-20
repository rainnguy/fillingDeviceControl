package com.banxian.controller.equip;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import com.banxian.entity.equip.AlarmInfoMap;
import com.banxian.entity.equip.AttrKeyMap;
import com.banxian.entity.equip.AttrStaMap;
import com.banxian.entity.equip.AttrValueMap;
import com.banxian.entity.equip.DeviceInfoMap;
import com.banxian.mapper.equip.AlarmInfoMapper;
import com.banxian.mapper.equip.CollectDataMapper;
import com.banxian.mapper.equip.DeviceInfoMapper;
import com.banxian.util.CurrentTime;
import com.banxian.util.FileUtil;
import com.banxian.util.Sequence;

/**
 * 解析数据并存入数据库
 * 
 * @author xk
 *
 */
public class AnalyseDataController {

	@Inject
	private DeviceInfoMapper deviceInfoMapper;

	@Inject
	private CollectDataMapper collectDataMapper;

	@Inject
	private AlarmInfoMapper alarmInfoMapper;

	/** FTP服务器的目录 */
	private static final String FTP_SERVER_FLOder = "E:\\stationDataCache";

	/** 储罐的类别号 */
	private static final String TANK = "01";

	/** 泵池的类别号 */
	private static final String PUMP = "02";

	/** 气化器的类别号 */
	private static final String CARBURETOR = "03";

	/** 流量计的类别号 */
	private static final String FLOWMETER = "04";

	/** 储罐区报警信息的类别号 */
	private static final String CONSTANT_TANK_KIND = "21";

	/** 撬装区报警信息的类别号 */
	private static final String CONSTANT_SKID_MOUNTED_KIND = "22";

	/** 流量计区报警信息的类别号 */
	private static final String CONSTANT_FLOWMETER_KIND = "24";

	/** 气化器区报警信息的类别号 */
	private static final String CONSTANT_CARBURETOR_KIND = "25";

	/** 其他类型报警信息的类别号 */
	private static final String CONSTANT_OTHER_KIND = "23";

	Sequence sequence = Sequence.getInstance();
	CurrentTime currentTime = CurrentTime.getInstance();

	/**
	 * 解析并获取实时数据
	 */
	public void runRealtime() {

		// 文件的目录
		// String folder = getFolder();

		File file = new File(FTP_SERVER_FLOder);
		// 判断文件夹是否存在
		if (!file.exists()) {
			return;
		}

		// 解析并获取实时信息
		getRealtimeInfo(FTP_SERVER_FLOder);

		// 解析并获取报警信息
		getAlarmInfo(FTP_SERVER_FLOder);
	}

	/**
	 * 解析并获取历史数据
	 */
	public void runEveryDay() {

		// 文件的目录
		// String folder = getFolder();

		File file = new File(FTP_SERVER_FLOder);
		// 判断文件夹是否存在
		if (!file.exists()) {
			return;
		}

		// 获取历史信息
		getHistoryInfo(FTP_SERVER_FLOder);
	}

	/**
	 * 每天凌晨创建必要的收集数据的文件
	 */
	public void runAtZero() {

		// 判断文件夹是否存在,如果不存在则创建文件夹
		checkAndCreateFloder(FTP_SERVER_FLOder);
		
		String floder = FTP_SERVER_FLOder + "\\RealtimeData";
		// 判断文件夹是否存在,如果不存在则创建文件夹
		checkAndCreateFloder(floder);

		floder = FTP_SERVER_FLOder + "\\RealtimeData\\" + currentTime.getNowDate();
		// 判断文件夹是否存在,如果不存在则创建文件夹
		checkAndCreateFloder(floder);

		floder = FTP_SERVER_FLOder + "\\HistoryData";
		// 判断文件夹是否存在,如果不存在则创建文件夹
		checkAndCreateFloder(floder);
		
		floder = FTP_SERVER_FLOder + "\\HistoryData\\" + currentTime.getNowMonth();
		// 判断文件夹是否存在,如果不存在则创建文件夹
		checkAndCreateFloder(floder);

		floder = FTP_SERVER_FLOder + "\\AlarmData";
		// 判断文件夹是否存在,如果不存在则创建文件夹
		checkAndCreateFloder(floder);
		
		floder = FTP_SERVER_FLOder + "\\AlarmData\\" + currentTime.getNowMonth();
		// 判断文件夹是否存在,如果不存在则创建文件夹
		checkAndCreateFloder(floder);
	}

	/**
	 * 判断文件夹是否存在,如果不存在则创建文件夹
	 */
	private void checkAndCreateFloder(String floder) {

		File file = new File(floder);
		// 判断文件夹是否存在,如果不存在则创建文件夹
		if (!file.exists()) {
			file.mkdir();
		}
	}

	/**
	 * 解析并获取实时信息
	 * 
	 * @param folder
	 */
	private void getRealtimeInfo(String folder) {

		// 获取实时信息的目录
		String realtimeFolder = folder + "RealtimeData\\" + currentTime.getNowDate();

		File file = new File(realtimeFolder);
		// 判断文件夹是否存在
		if (!file.exists()) {
			return;
		}

		List<String> fileNameList = getFileNames(realtimeFolder);
		if (fileNameList == null || fileNameList.size() == 0) {
			return;
		}

		// 获取文件名列表中不同站点的最新文件的索引
		List<Integer> indexList = getNewIndex(fileNameList);

		List<AttrValueMap> attrValueList = new ArrayList<AttrValueMap>();
		List<AttrStaMap> attrStaList = new ArrayList<AttrStaMap>();
		List<AttrKeyMap> attrKeyList = new ArrayList<AttrKeyMap>();
		for (int i = 0; i < indexList.size(); i++) {

			// 获取要解析的文件名
			String fileName = fileNameList.get(indexList.get(i));

			// 获取文件内容
			FileUtil fileUtil = FileUtil.getInstance();
			List<String> dataList = fileUtil.readFileByLines(realtimeFolder + "\\" + fileName);

			// 解析文件内容
			parseFile(dataList, attrValueList, attrStaList, attrKeyList);
		}

		// 把数据存入数据库
		if (attrKeyList.size() != 0) {
			collectDataMapper.insertAttrKeyTemp(attrKeyList);
		}
		if (attrStaList.size() != 0) {
			collectDataMapper.insertAttrStatusTemp(attrStaList);
		}
		if (attrValueList.size() != 0) {
			collectDataMapper.insertAttrValueTemp(attrValueList);
		}

		// 删除文件名列表中的临时文件
		deleteFiles(realtimeFolder);
	}

	/**
	 * 解析并获取报警信息
	 * 
	 * @param folder
	 */
	private void getAlarmInfo(String folder) {

		// 获取报警信息的目录
		String alarmInfoFolder = folder + "AlarmData\\" + currentTime.getNowMonth();

		File file = new File(alarmInfoFolder);
		// 判断文件夹是否存在
		if (!file.exists()) {
			return;
		}

		List<String> fileNameList = getFileNames(alarmInfoFolder);
		if (fileNameList == null || fileNameList.size() == 0) {
			return;
		}

		List<AlarmInfoMap> alarmInfoList = new ArrayList<AlarmInfoMap>();
		for (String fileName : fileNameList) {

			// 获取文件绝对路径
			String filePath = alarmInfoFolder + "\\" + fileName;

			// 获取文件内容
			FileUtil fileUtil = FileUtil.getInstance();
			List<String> dataList = fileUtil.readFileByLines(filePath);

			// 解析文件内容
			parseAlarm(dataList, alarmInfoList);

			// 把数据存入数据库
			if (alarmInfoList.size() != 0) {
				alarmInfoMapper.insertAlarmInfoTemp(alarmInfoList);
				alarmInfoMapper.insertAlarmInfo(alarmInfoList);
			}

			// 备份报警信息文件
			moveAlarmFile(folder, filePath, fileName);
		}
	}

	/**
	 * 解析并获取历史信息
	 * 
	 * @param folder
	 */
	private void getHistoryInfo(String folder) {

		// 获取历史信息的目录
		String historyDataFolder = folder + "HistoryData\\" + currentTime.getNowMonth();

		File file = new File(historyDataFolder);
		// 判断文件夹是否存在
		if (!file.exists()) {
			return;
		}

		List<String> fileNameList = getFileNames(historyDataFolder);
		if (fileNameList == null || fileNameList.size() == 0) {
			return;
		}

		// 获取文件名列表中要读取的文件的索引
		List<String> targetList = getTargetFiles(fileNameList);

		List<AttrValueMap> attrValueList = new ArrayList<AttrValueMap>();
		List<AttrStaMap> attrStaList = new ArrayList<AttrStaMap>();
		List<AttrKeyMap> attrKeyList = new ArrayList<AttrKeyMap>();
		if (targetList.size() != 0) {
			for (String fileName : targetList) {
				// 获取文件内容
				FileUtil fileUtil = FileUtil.getInstance();
				List<String> dataList = fileUtil.readFileByLines(historyDataFolder + "\\" + fileName);

				// 解析文件内容
				parseFile(dataList, attrValueList, attrStaList, attrKeyList);
			}
		}

		// 把数据存入数据库
		if (attrKeyList.size() != 0) {
			collectDataMapper.insertAttrKey(attrKeyList);
		}
		if (attrStaList.size() != 0) {
			collectDataMapper.insertAttrStatus(attrStaList);
		}
		if (attrValueList.size() != 0) {
			collectDataMapper.insertAttrValue(attrValueList);
		}
	}

	/**
	 * 解析报警内容
	 * 
	 * @param dataList
	 * @param alarmInfoList
	 */
	private void parseAlarm(List<String> dataList, List<AlarmInfoMap> alarmInfoList) {

		for (String data : dataList) {
			String[] dataTemp = data.split(",", -1);

			// 如果已读取，则忽略该条数据
			if (!"0".equals(dataTemp[0])) {
				continue;
			}

			String stationCode = dataTemp[1];
			String time = dataTemp[4];

			// 解析读取方式： 16位二进制数：0000 0000 0000 0000
			String readType = dataTemp[3];
			// 高8位的第一第二位
			String high12 = readType.substring(6, 8);
			// 低8位中低4位
			String lowlow4 = readType.substring(12);
			// 低8位中高4位
			String lowhigh4 = readType.substring(8, 12);

			if ("00".equals(high12)) {
				// 撬装区报警
				getSkidMountedOrCarburetorAlarm(lowlow4, stationCode, alarmInfoList, dataTemp, time, PUMP,
						CONSTANT_SKID_MOUNTED_KIND);

				// 其他类型报警
				if (dataTemp[13] != null && !dataTemp[13].isEmpty()) {
					// 追加第1个流量计区的报警
					getAlarmParam(alarmInfoList, dataTemp[13], stationCode, time, "", CONSTANT_OTHER_KIND);
				}
			} else if ("01".equals(high12)) {

				// 气化器区报警
				getSkidMountedOrCarburetorAlarm(lowlow4, stationCode, alarmInfoList, dataTemp, time, CARBURETOR,
						CONSTANT_CARBURETOR_KIND);

				// 高8位的第五第六位
				String high56 = readType.substring(2, 4);
				if ("01".equals(high56)) {
					// 1个流量计

					// 获取设备id的参数：站点code、获取数量、设备类型
					List<DeviceInfoMap> deviceInfoMapList = getDeviceId(stationCode, 1, FLOWMETER);

					// 追加第1个流量计区的报警
					getAlarmParam(alarmInfoList, dataTemp[13], stationCode, time,
							deviceInfoMapList.get(0).get("deviceId").toString(), CONSTANT_FLOWMETER_KIND);

				} else if ("10".equals(high56)) {
					// 2个流量计

					// 获取设备id的参数：站点code、获取数量、设备类型
					List<DeviceInfoMap> deviceInfoMapList = getDeviceId(stationCode, 2, FLOWMETER);

					// 追加第1个流量计区的报警
					getAlarmParam(alarmInfoList, dataTemp[13], stationCode, time,
							deviceInfoMapList.get(0).get("deviceId").toString(), CONSTANT_FLOWMETER_KIND);
					// 追加第1个流量计区的报警
					getAlarmParam(alarmInfoList, dataTemp[14], stationCode, time,
							deviceInfoMapList.get(1).get("deviceId").toString(), CONSTANT_FLOWMETER_KIND);
				}

				// 其他类型报警
				if (dataTemp[15] != null && !dataTemp[15].isEmpty()) {
					// 追加第1个流量计区的报警
					getAlarmParam(alarmInfoList, dataTemp[15], stationCode, time, "", CONSTANT_OTHER_KIND);
				}
			}

			if ("0001".equals(lowhigh4)) {
				// 1个储罐区

				// 获取设备id的参数：站点code、获取数量、设备类型
				List<DeviceInfoMap> deviceInfoMapList = getDeviceId(stationCode, 1, TANK);

				// 追加第1个储罐的报警
				getAlarmParam(alarmInfoList, dataTemp[5], stationCode, time,
						deviceInfoMapList.get(0).get("deviceId").toString(), CONSTANT_TANK_KIND);

			} else if ("0010".equals(lowhigh4)) {
				// 2个储罐区

				// 获取设备id的参数：站点code、获取数量、设备类型
				List<DeviceInfoMap> deviceInfoMapList = getDeviceId(stationCode, 2, TANK);

				// 追加第1个储罐的报警
				getAlarmParam(alarmInfoList, dataTemp[5], stationCode, time,
						deviceInfoMapList.get(0).get("deviceId").toString(), CONSTANT_TANK_KIND);

				// 追加第2个储罐的报警
				getAlarmParam(alarmInfoList, dataTemp[6], stationCode, time,
						deviceInfoMapList.get(1).get("deviceId").toString(), CONSTANT_TANK_KIND);

			} else if ("0011".equals(lowhigh4)) {
				// 3个储罐区

				// 获取设备id的参数：站点code、获取数量、设备类型
				List<DeviceInfoMap> deviceInfoMapList = getDeviceId(stationCode, 3, TANK);

				// 追加第1个储罐的报警
				getAlarmParam(alarmInfoList, dataTemp[5], stationCode, time,
						deviceInfoMapList.get(0).get("deviceId").toString(), CONSTANT_TANK_KIND);

				// 追加第2个储罐的报警
				getAlarmParam(alarmInfoList, dataTemp[6], stationCode, time,
						deviceInfoMapList.get(1).get("deviceId").toString(), CONSTANT_TANK_KIND);

				// 追加第3个储罐的报警
				getAlarmParam(alarmInfoList, dataTemp[7], stationCode, time,
						deviceInfoMapList.get(2).get("deviceId").toString(), CONSTANT_TANK_KIND);

			} else if ("0100".equals(lowhigh4)) {
				// 4个储罐区

				// 获取设备id的参数：站点code、获取数量、设备类型
				List<DeviceInfoMap> deviceInfoMapList = getDeviceId(stationCode, 4, TANK);

				// 追加第1个储罐的报警
				getAlarmParam(alarmInfoList, dataTemp[5], stationCode, time,
						deviceInfoMapList.get(0).get("deviceId").toString(), CONSTANT_TANK_KIND);

				// 追加第2个储罐的报警
				getAlarmParam(alarmInfoList, dataTemp[6], stationCode, time,
						deviceInfoMapList.get(1).get("deviceId").toString(), CONSTANT_TANK_KIND);

				// 追加第3个储罐的报警
				getAlarmParam(alarmInfoList, dataTemp[7], stationCode, time,
						deviceInfoMapList.get(2).get("deviceId").toString(), CONSTANT_TANK_KIND);

				// 追加第4个储罐的报警
				getAlarmParam(alarmInfoList, dataTemp[8], stationCode, time,
						deviceInfoMapList.get(3).get("deviceId").toString(), CONSTANT_TANK_KIND);
			}
		}
	}

	/**
	 * 撬装区或气化器区报警
	 * 
	 * @param lowlow4
	 * @param stationCode
	 * @param alarmInfoList
	 * @param dataTemp
	 * @param time
	 * @param type
	 * @param kind
	 */
	private void getSkidMountedOrCarburetorAlarm(String lowlow4, String stationCode, List<AlarmInfoMap> alarmInfoList,
			String[] dataTemp, String time, String type, String kind) {

		if ("0001".equals(lowlow4)) {
			// 1个区

			// 获取设备id的参数：站点code、获取数量、设备类型
			List<DeviceInfoMap> deviceInfoMapList = getDeviceId(stationCode, 1, type);

			// 追加第1个区的报警
			getAlarmParam(alarmInfoList, dataTemp[9], stationCode, time,
					deviceInfoMapList.get(0).get("deviceId").toString(), kind);

		} else if ("0010".equals(lowlow4)) {
			// 2个区

			// 获取设备id的参数：站点code、获取数量、设备类型
			List<DeviceInfoMap> deviceInfoMapList = getDeviceId(stationCode, 2, type);

			// 追加第1个区的报警
			getAlarmParam(alarmInfoList, dataTemp[9], stationCode, time,
					deviceInfoMapList.get(0).get("deviceId").toString(), kind);
			// 追加第2个区的报警
			getAlarmParam(alarmInfoList, dataTemp[10], stationCode, time,
					deviceInfoMapList.get(1).get("deviceId").toString(), kind);

		} else if ("0011".equals(lowlow4)) {
			// 3个区

			// 获取设备id的参数：站点code、获取数量、设备类型
			List<DeviceInfoMap> deviceInfoMapList = getDeviceId(stationCode, 3, type);

			// 追加第1个区的报警
			getAlarmParam(alarmInfoList, dataTemp[9], stationCode, time,
					deviceInfoMapList.get(0).get("deviceId").toString(), kind);
			// 追加第2个区的报警
			getAlarmParam(alarmInfoList, dataTemp[10], stationCode, time,
					deviceInfoMapList.get(1).get("deviceId").toString(), kind);
			// 追加第3个区的报警
			getAlarmParam(alarmInfoList, dataTemp[11], stationCode, time,
					deviceInfoMapList.get(2).get("deviceId").toString(), kind);

		} else if ("0100".equals(lowlow4)) {
			// 4个区

			// 获取设备id的参数：站点code、获取数量、设备类型
			List<DeviceInfoMap> deviceInfoMapList = getDeviceId(stationCode, 4, type);

			// 追加第1个区的报警
			getAlarmParam(alarmInfoList, dataTemp[9], stationCode, time,
					deviceInfoMapList.get(0).get("deviceId").toString(), kind);
			// 追加第2个区的报警
			getAlarmParam(alarmInfoList, dataTemp[10], stationCode, time,
					deviceInfoMapList.get(1).get("deviceId").toString(), kind);
			// 追加第3个区的报警
			getAlarmParam(alarmInfoList, dataTemp[11], stationCode, time,
					deviceInfoMapList.get(2).get("deviceId").toString(), kind);
			// 追加第4个区的报警
			getAlarmParam(alarmInfoList, dataTemp[12], stationCode, time,
					deviceInfoMapList.get(3).get("deviceId").toString(), kind);
		}
	}

	/**
	 * 解析文件内容
	 * 
	 * @param dataList
	 * @param attrValueList
	 * @param attrStaList
	 * @param attrKeyList
	 */
	private void parseFile(List<String> dataList, List<AttrValueMap> attrValueList, List<AttrStaMap> attrStaList,
			List<AttrKeyMap> attrKeyList) {

		for (String data : dataList) {

			String[] dataTemp = data.split(",", -1);

			AttrStaMap attrStaMap = new AttrStaMap();
			attrStaMap.put("station", dataTemp[0]);
			attrStaMap.put("time", dataTemp[3]);
			// 仪表风压力
			attrStaMap.put("meterPressure", dataTemp[84]);
			// 环境温度
			attrStaMap.put("envirTemp", dataTemp[85]);
			// 给attrStaList追加值
			attrStaList.add(attrStaMap);

			// 解析读取方式： 16位二进制数：0000 0000 0000 0000
			String readType = dataTemp[2];
			// 高8位的第一第二位
			String high12 = readType.substring(6, 8);
			// 低8位中低4位
			String lowlow4 = readType.substring(12);
			// 低8位中高4位
			String lowhigh4 = readType.substring(8, 12);

			if ("00".equals(high12)) {
				// 加气站
				if ("0001".equals(lowlow4)) {
					// 1个LNG泵

					// 主键idList
					List<String> idList = new LinkedList<String>();
					idList.add(sequence.nextId());

					// 追加第1个LNG泵的值
					getPump1(attrValueList, dataTemp, idList.get(0));

					// 对attrKeyList追加值
					addAttrKeyList(attrKeyList, idList, dataTemp[0], dataTemp[3], 1, PUMP);

				} else if ("0010".equals(lowlow4)) {
					// 2个LNG泵

					// 主键idList
					List<String> idList = new LinkedList<String>();
					for (int k = 0; k < 2; k++) {
						idList.add(sequence.nextId());
					}

					// 追加第1个LNG泵的值
					getPump1(attrValueList, dataTemp, idList.get(0));
					// 追加第2个LNG泵的值
					getPump2(attrValueList, dataTemp, idList.get(1));

					// 对attrKeyList追加值
					addAttrKeyList(attrKeyList, idList, dataTemp[0], dataTemp[3], 2, PUMP);

				} else if ("0011".equals(lowlow4)) {
					// 3个LNG泵

					// 主键idList
					List<String> idList = new LinkedList<String>();
					for (int k = 0; k < 3; k++) {
						idList.add(sequence.nextId());
					}

					// 追加第1个LNG泵的值
					getPump1(attrValueList, dataTemp, idList.get(0));
					// 追加第2个LNG泵的值
					getPump2(attrValueList, dataTemp, idList.get(1));
					// 追加第3个LNG泵的值
					getPump3(attrValueList, dataTemp, idList.get(2));

					// 对attrKeyList追加值
					addAttrKeyList(attrKeyList, idList, dataTemp[0], dataTemp[3], 3, PUMP);

				} else if ("0100".equals(lowlow4)) {
					// 4个LNG泵

					// 主键idList
					List<String> idList = new LinkedList<String>();
					for (int k = 0; k < 4; k++) {
						idList.add(sequence.nextId());
					}

					// 追加第1个LNG泵的值
					getPump1(attrValueList, dataTemp, idList.get(0));
					// 追加第2个LNG泵的值
					getPump2(attrValueList, dataTemp, idList.get(1));
					// 追加第3个LNG泵的值
					getPump3(attrValueList, dataTemp, idList.get(2));
					// 追加第4个LNG泵的值
					getPump4(attrValueList, dataTemp, idList.get(3));

					// 对attrKeyList追加值
					addAttrKeyList(attrKeyList, idList, dataTemp[0], dataTemp[3], 4, PUMP);
				}

				if ("0001".equals(lowhigh4)) {
					// 1个低温储罐

					// 主键idList
					List<String> idList = new LinkedList<String>();
					idList.add(sequence.nextId());

					// 追加第1个低温储罐的值
					getTank1(attrValueList, dataTemp, idList.get(0));

					// 对attrKeyList追加值
					addAttrKeyList(attrKeyList, idList, dataTemp[0], dataTemp[3], 1, TANK);

				} else if ("0010".equals(lowhigh4)) {
					// 2个低温储罐

					// 主键idList
					List<String> idList = new LinkedList<String>();
					for (int k = 0; k < 2; k++) {
						idList.add(sequence.nextId());
					}

					// 追加第1个低温储罐的值
					getTank1(attrValueList, dataTemp, idList.get(0));
					// 追加第2个低温储罐的值
					getTank2(attrValueList, dataTemp, idList.get(1));

					// 对attrKeyList追加值
					addAttrKeyList(attrKeyList, idList, dataTemp[0], dataTemp[3], 2, TANK);

				} else if ("0011".equals(lowhigh4)) {
					// 3个低温储罐

					// 主键idList
					List<String> idList = new LinkedList<String>();
					for (int k = 0; k < 3; k++) {
						idList.add(sequence.nextId());
					}

					// 追加第1个低温储罐的值
					getTank1(attrValueList, dataTemp, idList.get(0));
					// 追加第2个低温储罐的值
					getTank2(attrValueList, dataTemp, idList.get(1));
					// 追加第3个低温储罐的值
					getTank3(attrValueList, dataTemp, idList.get(2));

					// 对attrKeyList追加值
					addAttrKeyList(attrKeyList, idList, dataTemp[0], dataTemp[3], 3, TANK);

				} else if ("0100".equals(lowhigh4)) {
					// 4个低温储罐

					// 主键idList
					List<String> idList = new LinkedList<String>();
					for (int k = 0; k < 4; k++) {
						idList.add(sequence.nextId());
					}

					// 追加第1个低温储罐的值
					getTank1(attrValueList, dataTemp, idList.get(0));
					// 追加第2个低温储罐的值
					getTank2(attrValueList, dataTemp, idList.get(1));
					// 追加第3个低温储罐的值
					getTank3(attrValueList, dataTemp, idList.get(2));
					// 追加第4个低温储罐的值
					getTank4(attrValueList, dataTemp, idList.get(3));

					// 对attrKeyList追加值
					addAttrKeyList(attrKeyList, idList, dataTemp[0], dataTemp[3], 4, TANK);
				}

			} else if ("01".equals(high12)) {
				// 气化站
				if ("0001".equals(lowhigh4)) {
					// 1个低温储罐

					// 主键idList
					List<String> idList = new LinkedList<String>();
					idList.add(sequence.nextId());
					// 追加第1个低温储罐的值
					getTank1(attrValueList, dataTemp, idList.get(0));
					// 对attrKeyList追加值
					addAttrKeyList(attrKeyList, idList, dataTemp[0], dataTemp[3], 1, TANK);

				} else if ("0010".equals(lowhigh4)) {
					// 2个低温储罐

					// 主键idList
					List<String> idList = new LinkedList<String>();
					for (int k = 0; k < 2; k++) {
						idList.add(sequence.nextId());
					}
					// 追加第1个低温储罐的值
					getTank1(attrValueList, dataTemp, idList.get(0));
					// 追加第2个低温储罐的值
					getTank2(attrValueList, dataTemp, idList.get(1));
					// 对attrKeyList追加值
					addAttrKeyList(attrKeyList, idList, dataTemp[0], dataTemp[3], 2, TANK);

				} else if ("0011".equals(lowhigh4)) {
					// 3个低温储罐

					// 主键idList
					List<String> idList = new LinkedList<String>();
					for (int k = 0; k < 3; k++) {
						idList.add(sequence.nextId());
					}
					// 追加第1个低温储罐的值
					getTank1(attrValueList, dataTemp, idList.get(0));
					// 追加第2个低温储罐的值
					getTank2(attrValueList, dataTemp, idList.get(1));
					// 追加第3个低温储罐的值
					getTank3(attrValueList, dataTemp, idList.get(2));
					// 对attrKeyList追加值
					addAttrKeyList(attrKeyList, idList, dataTemp[0], dataTemp[3], 3, TANK);

				} else if ("0100".equals(lowhigh4)) {
					// 4个低温储罐

					// 主键idList
					List<String> idList = new LinkedList<String>();
					for (int k = 0; k < 4; k++) {
						idList.add(sequence.nextId());
					}
					// 追加第1个低温储罐的值
					getTank1(attrValueList, dataTemp, idList.get(0));
					// 追加第2个低温储罐的值
					getTank2(attrValueList, dataTemp, idList.get(1));
					// 追加第3个低温储罐的值
					getTank3(attrValueList, dataTemp, idList.get(2));
					// 追加第4个低温储罐的值
					getTank4(attrValueList, dataTemp, idList.get(3));
					// 对attrKeyList追加值
					addAttrKeyList(attrKeyList, idList, dataTemp[0], dataTemp[3], 4, TANK);
				}

				if ("0001".equals(lowlow4)) {
					// 1个气化器

					// 主键idList
					List<String> idList = new LinkedList<String>();
					idList.add(sequence.nextId());

					// 追加第1个气化器的值
					getCarburetor1(attrValueList, dataTemp, idList.get(0));

					// 对attrKeyList追加值
					addAttrKeyList(attrKeyList, idList, dataTemp[0], dataTemp[3], 1, CARBURETOR);

				} else if ("0010".equals(lowlow4)) {
					// 2个气化器

					// 主键idList
					List<String> idList = new LinkedList<String>();
					for (int k = 0; k < 2; k++) {
						idList.add(sequence.nextId());
					}

					// 追加第1个气化器的值
					getCarburetor1(attrValueList, dataTemp, idList.get(0));
					// 追加第2个气化器的值
					getCarburetor2(attrValueList, dataTemp, idList.get(1));

					// 对attrKeyList追加值
					addAttrKeyList(attrKeyList, idList, dataTemp[0], dataTemp[3], 2, CARBURETOR);

				} else if ("0011".equals(lowlow4)) {
					// 3个气化器

					// 主键idList
					List<String> idList = new LinkedList<String>();
					for (int k = 0; k < 3; k++) {
						idList.add(sequence.nextId());
					}

					// 追加第1个气化器的值
					getCarburetor1(attrValueList, dataTemp, idList.get(0));
					// 追加第2个气化器的值
					getCarburetor2(attrValueList, dataTemp, idList.get(1));
					// 追加第3个气化器的值
					getCarburetor3(attrValueList, dataTemp, idList.get(2));

					// 对attrKeyList追加值
					addAttrKeyList(attrKeyList, idList, dataTemp[0], dataTemp[3], 3, CARBURETOR);

				} else if ("0100".equals(lowlow4)) {
					// 4个气化器

					// 主键idList
					List<String> idList = new LinkedList<String>();
					for (int k = 0; k < 4; k++) {
						idList.add(sequence.nextId());
					}

					// 追加第1个气化器的值
					getCarburetor1(attrValueList, dataTemp, idList.get(0));
					// 追加第2个气化器的值
					getCarburetor2(attrValueList, dataTemp, idList.get(1));
					// 追加第3个气化器的值
					getCarburetor3(attrValueList, dataTemp, idList.get(2));
					// 追加第4个气化器的值
					getCarburetor4(attrValueList, dataTemp, idList.get(3));

					// 对attrKeyList追加值
					addAttrKeyList(attrKeyList, idList, dataTemp[0], dataTemp[3], 4, CARBURETOR);
				}

				// 高8位的第五第六位
				String high56 = readType.substring(2, 4);
				if ("01".equals(high56)) {
					// 1个流量计

					// 主键idList
					List<String> idList = new LinkedList<String>();
					idList.add(sequence.nextId());

					// 追加第1个流量计的值
					getFlowmeter1(attrValueList, dataTemp, idList.get(0));

					// 对attrKeyList追加值
					addAttrKeyList(attrKeyList, idList, dataTemp[0], dataTemp[3], 1, FLOWMETER);

				} else if ("10".equals(high56)) {
					// 2个流量计

					// 主键idList
					List<String> idList = new LinkedList<String>();
					for (int k = 0; k < 2; k++) {
						idList.add(sequence.nextId());
					}

					// 追加第1个流量计的值
					getFlowmeter1(attrValueList, dataTemp, idList.get(0));
					// 追加第2个流量计的值
					getFlowmeter2(attrValueList, dataTemp, idList.get(1));

					// 对attrKeyList追加值
					addAttrKeyList(attrKeyList, idList, dataTemp[0], dataTemp[3], 2, FLOWMETER);
				}
			}
		}
	}

	/**
	 * 追加第1个储罐的报警
	 * 
	 * @param alarmInfoList
	 * @param alarmInfo
	 * @param stationCode
	 * @param time
	 * @param deviceId
	 * @param constantKind
	 */
	private void getAlarmParam(List<AlarmInfoMap> alarmInfoList, String alarmInfo, String stationCode, String time,
			String deviceId, String constantKind) {

		if (alarmInfo.contains("1")) {

			int index = 0;
			while (index < alarmInfo.length()) {
				AlarmInfoMap map = new AlarmInfoMap();

				index = alarmInfo.indexOf("1", index);

				if (index != -1) {
					index++;

					map.put("constantValue", String.valueOf(index));
					map.put("constantKind", constantKind);
					map.put("deviceId", deviceId);
					map.put("time", time);

					alarmInfoList.add(map);

				} else {
					break;
				}
			}
		}
	}

	/**
	 * 对attrKeyList追加值
	 * 
	 * @param attrKeyList
	 * @param idList
	 * @param station
	 * @param time
	 * @param number
	 * @param type
	 */
	private void addAttrKeyList(List<AttrKeyMap> attrKeyList, List<String> idList, String station, String time,
			Integer number, String type) {
		// 获取设备id的参数：站点code、获取数量、设备类型
		List<DeviceInfoMap> deviceInfoMapList = getDeviceId(station, number, type);

		if (deviceInfoMapList != null) {
			for (int k = 0; k < number; k++) {
				// add to key list
				attrKeyList.add(setAttrKeyMap(idList.get(k), station, time,
						deviceInfoMapList.get(k).get("deviceId").toString()));
			}
		}
	}

	/**
	 * 给AttrKeyMap设值
	 * 
	 * @param mainCode
	 * @param station
	 * @param time
	 * @param deviceId
	 * @return
	 */
	private AttrKeyMap setAttrKeyMap(String mainCode, String station, String time, String deviceId) {

		AttrKeyMap attrKeyMap = new AttrKeyMap();

		attrKeyMap.put("mainCode", mainCode);
		attrKeyMap.put("station", station);
		attrKeyMap.put("time", time);
		attrKeyMap.put("deviceId", deviceId);

		return attrKeyMap;
	}

	/**
	 * 获取设备id
	 * 
	 * @param station
	 * @param number
	 * @param type
	 * @return
	 */
	private List<DeviceInfoMap> getDeviceId(String station, Integer number, String type) {
		DeviceInfoMap deviceInfoMap = new DeviceInfoMap();
		deviceInfoMap.put("station", station);
		deviceInfoMap.put("type", type);
		deviceInfoMap.put("number", number);
		List<DeviceInfoMap> deviceInfoMapList = deviceInfoMapper.findDeviceId(deviceInfoMap);

		return deviceInfoMapList;
	}

	/**
	 * 获取第1个低温储罐的数据
	 * 
	 * @param attrValueList
	 * @param dataTemp
	 * @param mainCode
	 */
	private void getTank1(List<AttrValueMap> attrValueList, String[] dataTemp, String mainCode) {

		AttrValueMap attrValueMap = new AttrValueMap();

		attrValueMap = setIdAndTime(mainCode, dataTemp[3]);
		attrValueMap.put("key", "储罐压力");
		attrValueMap.put("value", dataTemp[4]);
		attrValueList.add(attrValueMap);

		attrValueMap = setIdAndTime(mainCode, dataTemp[3]);
		attrValueMap.put("key", "LNG差压");
		attrValueMap.put("value", dataTemp[5]);
		attrValueList.add(attrValueMap);

		attrValueMap = setIdAndTime(mainCode, dataTemp[3]);
		attrValueMap.put("key", "LNG液位");
		attrValueMap.put("value", dataTemp[6]);
		attrValueList.add(attrValueMap);

		attrValueMap = setIdAndTime(mainCode, dataTemp[3]);
		attrValueMap.put("key", "LNG高度");
		attrValueMap.put("value", dataTemp[7]);
		attrValueList.add(attrValueMap);

		attrValueMap = setIdAndTime(mainCode, dataTemp[3]);
		attrValueMap.put("key", "LNG重量");
		attrValueMap.put("value", dataTemp[8]);
		attrValueList.add(attrValueMap);
	}

	/**
	 * 获取第2个低温储罐的数据
	 * 
	 * @param attrValueList
	 * @param dataTemp
	 * @param mainCode
	 */
	private void getTank2(List<AttrValueMap> attrValueList, String[] dataTemp, String mainCode) {

		AttrValueMap attrValueMap = new AttrValueMap();

		attrValueMap = setIdAndTime(mainCode, dataTemp[3]);
		attrValueMap.put("key", "储罐压力");
		attrValueMap.put("value", dataTemp[14]);
		attrValueList.add(attrValueMap);

		attrValueMap = setIdAndTime(mainCode, dataTemp[3]);
		attrValueMap.put("key", "LNG差压");
		attrValueMap.put("value", dataTemp[15]);
		attrValueList.add(attrValueMap);

		attrValueMap = setIdAndTime(mainCode, dataTemp[3]);
		attrValueMap.put("key", "LNG液位");
		attrValueMap.put("value", dataTemp[16]);
		attrValueList.add(attrValueMap);

		attrValueMap = setIdAndTime(mainCode, dataTemp[3]);
		attrValueMap.put("key", "LNG高度");
		attrValueMap.put("value", dataTemp[17]);
		attrValueList.add(attrValueMap);

		attrValueMap = setIdAndTime(mainCode, dataTemp[3]);
		attrValueMap.put("key", "LNG重量");
		attrValueMap.put("value", dataTemp[18]);
		attrValueList.add(attrValueMap);
	}

	/**
	 * 获取第3个低温储罐的数据
	 * 
	 * @param attrValueList
	 * @param dataTemp
	 * @param mainCode
	 */
	private void getTank3(List<AttrValueMap> attrValueList, String[] dataTemp, String mainCode) {

		AttrValueMap attrValueMap = new AttrValueMap();

		attrValueMap = setIdAndTime(mainCode, dataTemp[3]);
		attrValueMap.put("key", "储罐压力");
		attrValueMap.put("value", dataTemp[24]);
		attrValueList.add(attrValueMap);

		attrValueMap = setIdAndTime(mainCode, dataTemp[3]);
		attrValueMap.put("key", "LNG差压");
		attrValueMap.put("value", dataTemp[25]);
		attrValueList.add(attrValueMap);

		attrValueMap = setIdAndTime(mainCode, dataTemp[3]);
		attrValueMap.put("key", "LNG液位");
		attrValueMap.put("value", dataTemp[26]);
		attrValueList.add(attrValueMap);

		attrValueMap = setIdAndTime(mainCode, dataTemp[3]);
		attrValueMap.put("key", "LNG高度");
		attrValueMap.put("value", dataTemp[27]);
		attrValueList.add(attrValueMap);

		attrValueMap = setIdAndTime(mainCode, dataTemp[3]);
		attrValueMap.put("key", "LNG重量");
		attrValueMap.put("value", dataTemp[28]);
		attrValueList.add(attrValueMap);
	}

	/**
	 * 获取第4个低温储罐的数据
	 * 
	 * @param attrValueList
	 * @param dataTemp
	 * @param mainCode
	 */
	private void getTank4(List<AttrValueMap> attrValueList, String[] dataTemp, String mainCode) {

		AttrValueMap attrValueMap = new AttrValueMap();

		attrValueMap = setIdAndTime(mainCode, dataTemp[3]);
		attrValueMap.put("key", "储罐压力");
		attrValueMap.put("value", dataTemp[34]);
		attrValueList.add(attrValueMap);

		attrValueMap = setIdAndTime(mainCode, dataTemp[3]);
		attrValueMap.put("key", "LNG差压");
		attrValueMap.put("value", dataTemp[35]);
		attrValueList.add(attrValueMap);

		attrValueMap = setIdAndTime(mainCode, dataTemp[3]);
		attrValueMap.put("key", "LNG液位");
		attrValueMap.put("value", dataTemp[36]);
		attrValueList.add(attrValueMap);

		attrValueMap = setIdAndTime(mainCode, dataTemp[3]);
		attrValueMap.put("key", "LNG高度");
		attrValueMap.put("value", dataTemp[37]);
		attrValueList.add(attrValueMap);

		attrValueMap = setIdAndTime(mainCode, dataTemp[3]);
		attrValueMap.put("key", "LNG重量");
		attrValueMap.put("value", dataTemp[38]);
		attrValueList.add(attrValueMap);
	}

	/**
	 * 获取第1个LNG泵的数据
	 * 
	 * @param attrValueList
	 * @param dataTemp
	 * @param mainCode
	 */
	private void getPump1(List<AttrValueMap> attrValueList, String[] dataTemp, String mainCode) {
		AttrValueMap attrValueMap = new AttrValueMap();

		attrValueMap = setIdAndTime(mainCode, dataTemp[3]);
		attrValueMap.put("key", "泵前压力");
		attrValueMap.put("value", dataTemp[44]);
		attrValueList.add(attrValueMap);

		attrValueMap = setIdAndTime(mainCode, dataTemp[3]);
		attrValueMap.put("key", "泵后压力");
		attrValueMap.put("value", dataTemp[45]);
		attrValueList.add(attrValueMap);

		attrValueMap = setIdAndTime(mainCode, dataTemp[3]);
		attrValueMap.put("key", "泵池温度");
		attrValueMap.put("value", dataTemp[46]);
		attrValueList.add(attrValueMap);

		attrValueMap = setIdAndTime(mainCode, dataTemp[3]);
		attrValueMap.put("key", "变频器频率");
		attrValueMap.put("value", dataTemp[47]);
		attrValueList.add(attrValueMap);

		attrValueMap = setIdAndTime(mainCode, dataTemp[3]);
		attrValueMap.put("key", "变频器电流");
		attrValueMap.put("value", dataTemp[48]);
		attrValueList.add(attrValueMap);
	}

	/**
	 * 获取第2个LNG泵的数据
	 * 
	 * @param attrValueList
	 * @param dataTemp
	 * @param mainCode
	 */
	private void getPump2(List<AttrValueMap> attrValueList, String[] dataTemp, String mainCode) {

		AttrValueMap attrValueMap = new AttrValueMap();

		attrValueMap = setIdAndTime(mainCode, dataTemp[3]);
		attrValueMap.put("key", "泵前压力");
		attrValueMap.put("value", dataTemp[54]);
		attrValueList.add(attrValueMap);

		attrValueMap = setIdAndTime(mainCode, dataTemp[3]);
		attrValueMap.put("key", "泵后压力");
		attrValueMap.put("value", dataTemp[55]);
		attrValueList.add(attrValueMap);

		attrValueMap = setIdAndTime(mainCode, dataTemp[3]);
		attrValueMap.put("key", "泵池温度");
		attrValueMap.put("value", dataTemp[56]);
		attrValueList.add(attrValueMap);

		attrValueMap = setIdAndTime(mainCode, dataTemp[3]);
		attrValueMap.put("key", "变频器频率");
		attrValueMap.put("value", dataTemp[57]);
		attrValueList.add(attrValueMap);

		attrValueMap = setIdAndTime(mainCode, dataTemp[3]);
		attrValueMap.put("key", "变频器电流");
		attrValueMap.put("value", dataTemp[58]);
		attrValueList.add(attrValueMap);
	}

	/**
	 * 获取第3个LNG泵的数据
	 * 
	 * @param attrValueList
	 * @param dataTemp
	 * @param mainCode
	 */
	private void getPump3(List<AttrValueMap> attrValueList, String[] dataTemp, String mainCode) {

		AttrValueMap attrValueMap = new AttrValueMap();

		attrValueMap = setIdAndTime(mainCode, dataTemp[3]);
		attrValueMap.put("key", "泵前压力");
		attrValueMap.put("value", dataTemp[64]);
		attrValueList.add(attrValueMap);

		attrValueMap = setIdAndTime(mainCode, dataTemp[3]);
		attrValueMap.put("key", "泵后压力");
		attrValueMap.put("value", dataTemp[65]);
		attrValueList.add(attrValueMap);

		attrValueMap = setIdAndTime(mainCode, dataTemp[3]);
		attrValueMap.put("key", "泵池温度");
		attrValueMap.put("value", dataTemp[66]);
		attrValueList.add(attrValueMap);

		attrValueMap = setIdAndTime(mainCode, dataTemp[3]);
		attrValueMap.put("key", "变频器频率");
		attrValueMap.put("value", dataTemp[67]);
		attrValueList.add(attrValueMap);

		attrValueMap = setIdAndTime(mainCode, dataTemp[3]);
		attrValueMap.put("key", "变频器电流");
		attrValueMap.put("value", dataTemp[68]);
		attrValueList.add(attrValueMap);
	}

	/**
	 * 获取第4个LNG泵的数据
	 * 
	 * @param attrValueList
	 * @param dataTemp
	 * @param mainCode
	 */
	private void getPump4(List<AttrValueMap> attrValueList, String[] dataTemp, String mainCode) {

		AttrValueMap attrValueMap = new AttrValueMap();

		attrValueMap = setIdAndTime(mainCode, dataTemp[3]);
		attrValueMap.put("key", "泵前压力");
		attrValueMap.put("value", dataTemp[74]);
		attrValueList.add(attrValueMap);

		attrValueMap = setIdAndTime(mainCode, dataTemp[3]);
		attrValueMap.put("key", "泵后压力");
		attrValueMap.put("value", dataTemp[75]);
		attrValueList.add(attrValueMap);

		attrValueMap = setIdAndTime(mainCode, dataTemp[3]);
		attrValueMap.put("key", "泵池温度");
		attrValueMap.put("value", dataTemp[76]);
		attrValueList.add(attrValueMap);

		attrValueMap = setIdAndTime(mainCode, dataTemp[3]);
		attrValueMap.put("key", "变频器频率");
		attrValueMap.put("value", dataTemp[77]);
		attrValueList.add(attrValueMap);

		attrValueMap = setIdAndTime(mainCode, dataTemp[3]);
		attrValueMap.put("key", "变频器电流");
		attrValueMap.put("value", dataTemp[78]);
		attrValueList.add(attrValueMap);
	}

	/**
	 * 获取第1个气化器的数据
	 * 
	 * @param attrValueList
	 * @param dataTemp
	 * @param mainCode
	 */
	private void getCarburetor1(List<AttrValueMap> attrValueList, String[] dataTemp, String mainCode) {

		AttrValueMap attrValueMap = new AttrValueMap();

		attrValueMap = setIdAndTime(mainCode, dataTemp[3]);
		attrValueMap.put("key", "汽化器后压力");
		attrValueMap.put("value", dataTemp[44]);
		attrValueList.add(attrValueMap);

		attrValueMap = setIdAndTime(mainCode, dataTemp[3]);
		attrValueMap.put("key", "汽化器后温度");
		attrValueMap.put("value", dataTemp[45]);
		attrValueList.add(attrValueMap);
	}

	/**
	 * 获取第2个气化器的数据
	 * 
	 * @param attrValueList
	 * @param dataTemp
	 * @param mainCode
	 */
	private void getCarburetor2(List<AttrValueMap> attrValueList, String[] dataTemp, String mainCode) {

		AttrValueMap attrValueMap = new AttrValueMap();

		attrValueMap = setIdAndTime(mainCode, dataTemp[3]);
		attrValueMap.put("key", "汽化器后压力");
		attrValueMap.put("value", dataTemp[49]);
		attrValueList.add(attrValueMap);

		attrValueMap = setIdAndTime(mainCode, dataTemp[3]);
		attrValueMap.put("key", "汽化器后温度");
		attrValueMap.put("value", dataTemp[50]);
		attrValueList.add(attrValueMap);
	}

	/**
	 * 获取第3个气化器的数据
	 * 
	 * @param attrValueList
	 * @param dataTemp
	 * @param mainCode
	 */
	private void getCarburetor3(List<AttrValueMap> attrValueList, String[] dataTemp, String mainCode) {

		AttrValueMap attrValueMap = new AttrValueMap();

		attrValueMap = setIdAndTime(mainCode, dataTemp[3]);
		attrValueMap.put("key", "汽化器后压力");
		attrValueMap.put("value", dataTemp[54]);
		attrValueList.add(attrValueMap);

		attrValueMap = setIdAndTime(mainCode, dataTemp[3]);
		attrValueMap.put("key", "汽化器后温度");
		attrValueMap.put("value", dataTemp[55]);
		attrValueList.add(attrValueMap);
	}

	/**
	 * 获取第4个气化器的数据
	 * 
	 * @param attrValueList
	 * @param dataTemp
	 * @param mainCode
	 */
	private void getCarburetor4(List<AttrValueMap> attrValueList, String[] dataTemp, String mainCode) {

		AttrValueMap attrValueMap = new AttrValueMap();

		attrValueMap = setIdAndTime(mainCode, dataTemp[3]);
		attrValueMap.put("key", "汽化器后压力");
		attrValueMap.put("value", dataTemp[59]);
		attrValueList.add(attrValueMap);

		attrValueMap = setIdAndTime(mainCode, dataTemp[3]);
		attrValueMap.put("key", "汽化器后温度");
		attrValueMap.put("value", dataTemp[60]);
		attrValueList.add(attrValueMap);
	}

	/**
	 * 获取第1个流量计的数据
	 * 
	 * @param attrValueList
	 * @param dataTemp
	 * @param mainCode
	 */
	private void getFlowmeter1(List<AttrValueMap> attrValueList, String[] dataTemp, String mainCode) {

		AttrValueMap attrValueMap = new AttrValueMap();

		attrValueMap = setIdAndTime(mainCode, dataTemp[3]);
		attrValueMap.put("key", "流量计温度");
		attrValueMap.put("value", dataTemp[64]);
		attrValueList.add(attrValueMap);

		attrValueMap = setIdAndTime(mainCode, dataTemp[3]);
		attrValueMap.put("key", "流量计瞬时流量");
		attrValueMap.put("value", dataTemp[65]);
		attrValueList.add(attrValueMap);

		attrValueMap = setIdAndTime(mainCode, dataTemp[3]);
		attrValueMap.put("key", "流量计压力");
		attrValueMap.put("value", dataTemp[66]);
		attrValueList.add(attrValueMap);

		attrValueMap = setIdAndTime(mainCode, dataTemp[3]);
		attrValueMap.put("key", "流量计总流量");
		attrValueMap.put("value", dataTemp[67]);
		attrValueList.add(attrValueMap);
	}

	/**
	 * 获取第2个流量计的数据
	 * 
	 * @param attrValueList
	 * @param dataTemp
	 * @param mainCode
	 */
	private void getFlowmeter2(List<AttrValueMap> attrValueList, String[] dataTemp, String mainCode) {

		AttrValueMap attrValueMap = new AttrValueMap();

		attrValueMap = setIdAndTime(mainCode, dataTemp[3]);
		attrValueMap.put("key", "流量计温度");
		attrValueMap.put("value", dataTemp[74]);
		attrValueList.add(attrValueMap);

		attrValueMap = setIdAndTime(mainCode, dataTemp[3]);
		attrValueMap.put("key", "流量计瞬时流量");
		attrValueMap.put("value", dataTemp[75]);
		attrValueList.add(attrValueMap);

		attrValueMap = setIdAndTime(mainCode, dataTemp[3]);
		attrValueMap.put("key", "流量计压力");
		attrValueMap.put("value", dataTemp[76]);
		attrValueList.add(attrValueMap);

		attrValueMap = setIdAndTime(mainCode, dataTemp[3]);
		attrValueMap.put("key", "流量计总流量");
		attrValueMap.put("value", dataTemp[77]);
		attrValueList.add(attrValueMap);
	}

	/**
	 * 设定id和时间
	 * 
	 * @param mainCode
	 * @param time
	 * @return
	 */
	private AttrValueMap setIdAndTime(String mainCode, String time) {

		AttrValueMap attrValueMap = new AttrValueMap();

		attrValueMap.put("mainCode", mainCode);
		attrValueMap.put("time", time);

		return attrValueMap;
	}

	/**
	 * 获取临时文件的目录
	 * 
	 * @return
	 */
	private String getFolder() {
		// "我的文档"目录
		javax.swing.filechooser.FileSystemView fsv = javax.swing.filechooser.FileSystemView.getFileSystemView();
		String myDocuments = fsv.getDefaultDirectory().getPath();

		return myDocuments + "\\stationDataCache\\";
	}

	/**
	 * 获取目录下所有文件的文件名
	 * 
	 * @param path
	 * @return
	 */
	private List<String> getFileNames(String path) {
		// get file list where the path has
		File file = new File(path);
		// get the folder list
		File[] array = file.listFiles();
		List<String> fileNameList = new ArrayList<String>();

		for (int i = 0; i < array.length; i++) {
			if (array[i].isFile()) {
				// only take file name
				fileNameList.add(array[i].getName());
			}
		}

		return fileNameList;
	}

	/**
	 * 获取目录下所有文件的绝对路径
	 * 
	 * @param path
	 * @return
	 */
	private List<String> getFilePath(String path) {
		// get file list where the path has
		File file = new File(path);
		// get the folder list
		File[] array = file.listFiles();
		List<String> filePathList = new ArrayList<String>();

		for (int i = 0; i < array.length; i++) {
			if (array[i].isFile()) {
				// take file path and name
				filePathList.add(array[i].getPath());
			}
		}

		return filePathList;
	}

	/**
	 * 获取文件名列表中不同站点的最新文件的索引
	 * 
	 * @param fileNameList
	 * @return
	 */
	private List<Integer> getNewIndex(List<String> fileNameList) {

		// 文件名列表中需要解析的文件的索引
		List<Integer> indexList = new ArrayList<Integer>();
		// 文件名列表中不需要遍历的文件的索引
		List<Integer> invalidIndexList = new ArrayList<Integer>();

		for (int i = 0; i < fileNameList.size(); i++) {
			if (!invalidIndexList.contains(i)) {
				String newName = fileNameList.get(i);
				String station1 = newName.substring(0, 8);
				String maxTime = newName.substring(9);
				Integer indexTemp = i;

				for (int j = i; j < fileNameList.size(); j++) {
					if (!invalidIndexList.contains(j)) {
						String anotherName = fileNameList.get(j);
						String station2 = anotherName.substring(0, 8);
						if (station1.equals(station2)) {
							String time2 = anotherName.substring(9);
							if (maxTime.compareTo(time2) < 0) {
								maxTime = time2;
								indexTemp = j;
							}
							invalidIndexList.add(j);
						}
					}
				}
				indexList.add(indexTemp);
			}
		}

		return indexList;
	}

	/**
	 * 获取文件名列表中需要解析的文件的索引
	 * 
	 * @param fileNameList
	 * @return
	 */
	private List<String> getTargetFiles(List<String> fileNameList) {

		CurrentTime ct = CurrentTime.getInstance();

		// 文件名列表中需要解析的文件的索引
		List<String> targetList = new ArrayList<String>();
		String lastDay = ct.getLastDay();

		for (String fileName : fileNameList) {
			if (lastDay.equals(fileName.substring(9, 17))) {
				targetList.add(fileName);
			}
		}
		return targetList;
	}

	/**
	 * 删除列表的文件
	 * 
	 * @param folder
	 */
	private void deleteFiles(String folder) {

		// 获取要删除的文件的绝对路径
		List<String> filePathList = getFilePath(folder);

		if (filePathList != null && filePathList.size() != 0) {
			for (String filePath : filePathList) {
				File file = new File(filePath);
				if (file.exists()) {
					file.delete();
				}
			}
		}
	}

	/**
	 * 备份报警信息文件
	 * 
	 * @param folder
	 * @param filePath
	 * @param fileName
	 */
	private void moveAlarmFile(String folder, String filePath, String fileName) {

		String backupPath2 = null;
		try {
			// 判断备份目录存不存在，不存在则创建
			String backupPath = folder + "AlarmData\\backup";

			File file1 = new File(backupPath);
			// 判断文件夹是否存在,如果不存在则创建文件夹
			if (!file1.exists()) {
				file1.mkdir();
			}

			backupPath2 = backupPath + "\\" + currentTime.getNowMonth();
			File file2 = new File(backupPath2);
			// 判断文件夹是否存在,如果不存在则创建文件夹
			if (!file2.exists()) {
				file2.mkdir();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 移动报警文件到备份目录
		moveFile(filePath, backupPath2 + "\\" + fileName);
	}

	/**
	 * 移动文件到指定目录
	 * 
	 * @param oldFilePath
	 * @param newFilePath
	 */
	private void moveFile(String oldFilePath, String newFilePath) {

		try {
			File afile = new File(oldFilePath);
			afile.renameTo(new File(newFilePath));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
