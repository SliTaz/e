package com.zbensoft.e.payment.api.control;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.github.pagehelper.PageHelper;
import com.zbensoft.e.payment.api.common.CommonLogImpl;
import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.HttpRestStatusFactory;
import com.zbensoft.e.payment.api.common.IDGenerate;
import com.zbensoft.e.payment.api.common.LocaleMessageSourceService;
import com.zbensoft.e.payment.api.common.PageHelperUtil;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.service.api.AlarmInfoService;
import com.zbensoft.e.payment.api.service.api.AlarmLevelService;
import com.zbensoft.e.payment.api.service.api.AlarmMangerService;
import com.zbensoft.e.payment.db.domain.AlarmInfo;
import com.zbensoft.e.payment.db.domain.AlarmLevel;
import com.zbensoft.e.payment.db.domain.AlarmManger;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/alarmInfo")
@RestController
public class AlarmInfoController {
	@Autowired
	AlarmInfoService alarmInfoService;
	@Autowired
	AlarmMangerService alarmMangerService;
	@Autowired
	AlarmLevelService alarmLevelService;
	
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	//查询通知，支持分页
	@PreAuthorize("hasRole('R_ALARM_I_Q')")
	@ApiOperation(value = "Query AlarmInfo，Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<AlarmInfo>> selectPage(
			@RequestParam(required = false) String id,
			@RequestParam(required = false) String alarmLevelCode,
			@RequestParam(required = false) String alarmOrigin,
			@RequestParam(required = false) Integer status,
			@RequestParam(required = false) String alarmTimeStart,
			@RequestParam(required = false) String alarmTimeEnd,
			@RequestParam(required = false) String handleTimeStart,
			@RequestParam(required = false) String handleTimeEnd,
			@RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		AlarmInfo alarmInfo = new AlarmInfo();
		alarmInfo.setAlarmInfoCode(id);
		alarmInfo.setAlarmLevelCode(alarmLevelCode);
		alarmInfo.setAlarmOrigin(alarmOrigin);
		if(status!=null){
		alarmInfo.setStatus(status);
		}
		alarmInfo.setAlarmTimeStart(alarmTimeStart);
		alarmInfo.setAlarmTimeEnd(alarmTimeEnd);
		alarmInfo.setHandleTimeStart(handleTimeStart);
		alarmInfo.setHandleTimeEnd(handleTimeEnd);
		List<AlarmInfo> list = new ArrayList<AlarmInfo>();
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = alarmInfoService.selectPage(alarmInfo);

		} else {
			list = alarmInfoService.selectPage(alarmInfo);
		}

		int count = alarmInfoService.count(alarmInfo);
		// 分页 end
		
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<AlarmInfo>>(new ArrayList<AlarmInfo>(),HttpRestStatus.NOT_FOUND);
		}
		
		List<AlarmInfo> listNew = new ArrayList<AlarmInfo>();
		for(AlarmInfo bean:list){
			AlarmLevel alarmLevel = alarmLevelService.selectByPrimaryKey(bean.getAlarmLevelCode());
			AlarmManger alarmManger = alarmMangerService.selectByPrimaryKey(bean.getAlarmOrigin());
			if(alarmLevel!=null){
				bean.setAlarmLevelName(alarmLevel.getName());
			}
			if(alarmManger!=null){
				bean.setAlarmOriginName(alarmManger.getName());
			}
			listNew.add(bean);
		}
		
		
		return new ResponseRestEntity<List<AlarmInfo>>(list, HttpRestStatus.OK,count,count);
	}

	//查询通知
	@PreAuthorize("hasRole('R_ALARM_I_Q')")
	@ApiOperation(value = "Query AlarmInfo", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<AlarmInfo> selectByPrimaryKey(@PathVariable("id") String id) {
		AlarmInfo alarmInfo = alarmInfoService.selectByPrimaryKey(id);
		if (alarmInfo == null) {
			return new ResponseRestEntity<AlarmInfo>(HttpRestStatus.NOT_FOUND);
		}

			
			AlarmManger alarmManger = alarmMangerService.selectByPrimaryKey(alarmInfo.getAlarmOrigin());
	
			if(alarmManger!=null){
				alarmInfo.setAlarmOriginName(alarmManger.getName());
			}
	
		return new ResponseRestEntity<AlarmInfo>(alarmInfo, HttpRestStatus.OK);
	}

	//新增通知
	@PreAuthorize("hasRole('R_ALARM_I_E')")
	@ApiOperation(value = "Add AlarmInfo", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> create(@Valid @RequestBody AlarmInfo alarmInfo, BindingResult result, UriComponentsBuilder ucBuilder) {

		alarmInfo.setAlarmInfoCode(IDGenerate.generateCommTwo(IDGenerate.ALARM_INFO));

		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}
		
		alarmInfoService.insert(alarmInfo);
		//新增日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, alarmInfo,CommonLogImpl.ALARM);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/alarmInfo/{id}").buildAndExpand(alarmInfo.getAlarmInfoCode()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,localeMessageSourceService.getMessage("common.create.created.message"));
	}

	//修改通知信息
	@PreAuthorize("hasRole('R_ALARM_I_E')")
	@ApiOperation(value = "Edit AlarmInfo", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<AlarmInfo> update(@PathVariable("id") String id, @Valid @RequestBody AlarmInfo alarmInfo, BindingResult result) {

		AlarmInfo currentAlarmInfo = alarmInfoService.selectByPrimaryKey(id);

		if (currentAlarmInfo == null) {
			return new ResponseRestEntity<AlarmInfo>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}

		currentAlarmInfo.setAlarmLevelCode(alarmInfo.getAlarmLevelCode());
		currentAlarmInfo.setAlarmTime(alarmInfo.getAlarmTime());
		currentAlarmInfo.setAlarmOrigin(alarmInfo.getAlarmOrigin());
		currentAlarmInfo.setContent(alarmInfo.getContent());
		currentAlarmInfo.setHandle(alarmInfo.getHandle());
		currentAlarmInfo.setHandleTime(alarmInfo.getHandleTime());
		currentAlarmInfo.setStatus(alarmInfo.getStatus());
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<AlarmInfo>(currentAlarmInfo,HttpRestStatusFactory.createStatus(list),HttpRestStatusFactory.createStatusMessage(list));
		}
		
		alarmInfoService.updateByPrimaryKey(currentAlarmInfo);
		//修改日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentAlarmInfo,CommonLogImpl.ALARM);
		return new ResponseRestEntity<AlarmInfo>(currentAlarmInfo, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	//修改部分通知信息
	@PreAuthorize("hasRole('R_ALARM_I_E')")
	@ApiOperation(value = "Edit Part AlarmInfo", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<AlarmInfo> updateSelective(@PathVariable("id") String id, @RequestBody AlarmInfo alarmInfo) {

		AlarmInfo currentAlarmInfo = alarmInfoService.selectByPrimaryKey(id);

		if (currentAlarmInfo == null) {
			return new ResponseRestEntity<AlarmInfo>(HttpRestStatus.NOT_FOUND);
		}
		currentAlarmInfo.setAlarmInfoCode(id);
		currentAlarmInfo.setAlarmLevelCode(alarmInfo.getAlarmLevelCode());
		currentAlarmInfo.setAlarmTime(alarmInfo.getAlarmTime());
		currentAlarmInfo.setAlarmOrigin(alarmInfo.getAlarmOrigin());
		currentAlarmInfo.setAlarmInfoCode(alarmInfo.getContent());
		currentAlarmInfo.setHandle(alarmInfo.getHandle());
		currentAlarmInfo.setHandleTime(alarmInfo.getHandleTime());
		currentAlarmInfo.setStatus(alarmInfo.getStatus());
		alarmInfoService.updateByPrimaryKeySelective(alarmInfo);
		//修改日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, alarmInfo,CommonLogImpl.ALARM);
		return new ResponseRestEntity<AlarmInfo>(currentAlarmInfo, HttpRestStatus.OK);
	}

	//删除指定通知
	@PreAuthorize("hasRole('R_ALARM_I_E')")
	@ApiOperation(value = "Delete AlarmInfo", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<AlarmInfo> delete(@PathVariable("id") String id) {

		AlarmInfo alarmInfo = alarmInfoService.selectByPrimaryKey(id);
		if (alarmInfo == null) {
			return new ResponseRestEntity<AlarmInfo>(HttpRestStatus.NOT_FOUND);
		}

		alarmInfoService.deleteByPrimaryKey(id);
		//删除日志开始
		AlarmInfo delBean = new AlarmInfo();
		delBean.setAlarmInfoCode(id);              
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, delBean,CommonLogImpl.ALARM);
		//删除日志结束
		return new ResponseRestEntity<AlarmInfo>(HttpRestStatus.NO_CONTENT);
	}

	
	@ApiOperation(value = "getAlarmRecords", notes = "")
	@RequestMapping(value = "/getAlarmRecords", method = RequestMethod.POST)
	public ResponseRestEntity<List<AlarmInfo>> getAlarmRecords(@RequestBody AlarmInfo alarmInfo) {
		
		List<AlarmInfo> list = new ArrayList<AlarmInfo>();
		int count = alarmInfoService.getRecordsCount(alarmInfo);
		
		if (count == 0) {
			return new ResponseRestEntity<List<AlarmInfo>>(new ArrayList<AlarmInfo>(), HttpRestStatus.NOT_FOUND);
		}
		
		PageHelper.startPage(1, 10);
		list = alarmInfoService.selectNewestRecords(alarmInfo);
		
		List<AlarmInfo> listNew = new ArrayList<AlarmInfo>();
		for(AlarmInfo bean:list){
			AlarmLevel alarmLevel = alarmLevelService.selectByPrimaryKey(bean.getAlarmLevelCode());
			AlarmManger alarmManger = alarmMangerService.selectByPrimaryKey(bean.getAlarmOrigin());
			if(alarmLevel!=null){
				bean.setAlarmLevelName(alarmLevel.getName());
				bean.setUnHandledCount(String.valueOf(count));
			}
			
			if(alarmManger!=null){
				bean.setAlarmOriginName(alarmManger.getName());
			}
			listNew.add(bean);
		}
		return new ResponseRestEntity<List<AlarmInfo>>(listNew, HttpRestStatus.OK, count, count);
	}
	
	
	
	//修改通知信息
	@PreAuthorize("hasRole('R_ALARM_I_E')")
	@ApiOperation(value = "Add Handle Advice", notes = "")
	@RequestMapping(value = "/handleView/{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<AlarmInfo> handleAdvice(@PathVariable("id") String id, @Valid @RequestBody AlarmInfo alarmInfo, BindingResult result) {
		String[] idArr = id.split(",");
		if(idArr!=null){
			for(String idStr :idArr){
				AlarmInfo currentAlarmInfo = alarmInfoService.selectByPrimaryKey(idStr);
				if(alarmInfo.getHandle()!=null&&alarmInfo.getHandle().length()>0){//修复报错2017-11-28 Chen
					currentAlarmInfo.setHandle(alarmInfo.getHandle());
				}
				currentAlarmInfo.setStatus(2);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				currentAlarmInfo.setHandleTime(sdf.format(new Date()));
				alarmInfoService.updateByPrimaryKey(currentAlarmInfo);
			}
		}

	 return new ResponseRestEntity<AlarmInfo>(HttpRestStatus.OK);
	}

	//批量
	@PreAuthorize("hasRole('R_ALARM_I_E')")
	@ApiOperation(value = "Delete Many SysLogs", notes = "")
	@RequestMapping(value = "/deleteMany/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<AlarmInfo> deleteSysLogMany(@PathVariable("id") String id) {
        String[] idStr = id.split(",");
        if(idStr!=null){
        	for(String str :idStr){
        		alarmInfoService.deleteByPrimaryKey(str);
        	}
        }
		return new ResponseRestEntity<AlarmInfo>(HttpRestStatus.NO_CONTENT);
	}
}