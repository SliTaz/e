package com.zbensoft.e.payment.api.control;

import java.util.ArrayList;
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
import com.zbensoft.e.payment.api.common.LocaleMessageSourceService;
import com.zbensoft.e.payment.api.common.PageHelperUtil;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.service.api.AlarmLevelService;
import com.zbensoft.e.payment.api.service.api.AlarmLevelTypeService;
import com.zbensoft.e.payment.api.service.api.AlarmTypeService;
import com.zbensoft.e.payment.db.domain.AlarmLevel;
import com.zbensoft.e.payment.db.domain.AlarmLevelType;
import com.zbensoft.e.payment.db.domain.AlarmType;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/alarmLevelType")
@RestController
public class AlarmLevelTypeController {
	@Autowired
	AlarmLevelTypeService alarmLevelTypeService;

	@Autowired
	AlarmLevelService alarmLevelService;
	
	@Autowired
	AlarmTypeService alarmTypeService;
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	@PreAuthorize("hasRole('R_ALARM_ALT_Q')")
	@ApiOperation(value = "Query AlarmLevelType，Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<AlarmLevelType>> selectPage(@RequestParam(required = false) String id,
			@RequestParam(required = false) String alarmTypeCode,
			@RequestParam(required = false) String start, @RequestParam(required = false) String length) {
		AlarmLevelType alarmLevelType = new AlarmLevelType();
		alarmLevelType.setAlarmLevelCode(id);
		alarmLevelType.setAlarmTypeCode(alarmTypeCode);
		int count = alarmLevelTypeService.count(alarmLevelType);
		if (count == 0) {
			return new ResponseRestEntity<List<AlarmLevelType>>(new ArrayList<AlarmLevelType>(), HttpRestStatus.NOT_FOUND);
		}
		List<AlarmLevelType> list = null;
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = alarmLevelTypeService.selectPage(alarmLevelType);
			// System.out.println("pageNum:"+pageNum+";pageSize:"+pageSize);
			// System.out.println("list.size:"+list.size());

		} else {
			list = alarmLevelTypeService.selectPage(alarmLevelType);
		}
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<AlarmLevelType>>(new ArrayList<AlarmLevelType>(),
					HttpRestStatus.NOT_FOUND);
		}
		List<AlarmLevelType> listNew = new ArrayList<AlarmLevelType>();
		for(AlarmLevelType bean:list){
			AlarmLevel alarmLevel = alarmLevelService.selectByPrimaryKey(bean.getAlarmLevelCode());
			AlarmType  alarmType = alarmTypeService.selectByPrimaryKey(bean.getAlarmTypeCode());
			if(alarmLevel!=null){
				bean.setAlarmLevelName(alarmLevel.getName());
			}
			if(alarmType!=null){
				bean.setAlarmTypeName(alarmType.getName());
			}
			listNew.add(bean);
		}
		return new ResponseRestEntity<List<AlarmLevelType>>(listNew, HttpRestStatus.OK, count, count);
	}

	@PreAuthorize("hasRole('R_ALARM_ALT_Q')")
	@ApiOperation(value = "Query AlarmLevelType", notes = "")
	@RequestMapping(value = "/{alarmLevelCode}/{alarmTypeCode}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<AlarmLevelType> selectByPrimaryKey(@PathVariable("alarmLevelCode") String alarmLevelCode,
			@PathVariable("alarmTypeCode") String alarmTypeCode) {
		AlarmLevelType bean = new AlarmLevelType();
		bean.setAlarmLevelCode(alarmLevelCode);
		bean.setAlarmTypeCode(alarmTypeCode);
		AlarmLevelType alarmLevelType = alarmLevelTypeService.selectByPrimaryKey(bean);
		if (alarmLevelType == null) {
			return new ResponseRestEntity<AlarmLevelType>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<AlarmLevelType>(alarmLevelType, HttpRestStatus.OK);
	}
	

	@PreAuthorize("hasRole('R_ALARM_ALT_Q')")
	@ApiOperation(value = "Query PayAppGateway", notes = "")
	@RequestMapping(value = "/{alarmLevelCode}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<List<AlarmLevelType>> selectByAlarmLevelId(@PathVariable("alarmLevelCode") String alarmLevelCode) {
		List<AlarmLevelType> list = alarmLevelTypeService.selectByAlarmLevelId(alarmLevelCode);
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<AlarmLevelType>>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<List<AlarmLevelType>>(list, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_ALARM_ALT_E')")
	@ApiOperation(value = "Add AlarmLevelType", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createAlarmLevelType(@Valid @RequestBody AlarmLevelType alarmLevelType,
			BindingResult result, UriComponentsBuilder ucBuilder) {
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}
		if(alarmLevelType.getAlarmTypeCode()!=null){
			String[] arr = alarmLevelType.getAlarmTypeCode().split(",");
			for(String typeId : arr){
				alarmLevelType.setAlarmTypeCode(typeId);
				AlarmLevelType bean = alarmLevelTypeService.selectByPrimaryKey(alarmLevelType);
				if(bean!=null){
					return new ResponseRestEntity<Void>(HttpRestStatus.CONFLICT,localeMessageSourceService.getMessage("common.create.conflict.message"));
				}
				alarmLevelTypeService.insert(alarmLevelType);
			}
		}
		//新增日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, alarmLevelType,CommonLogImpl.ALARM);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(
				ucBuilder.path("/alarmLevelType/{id}").buildAndExpand(alarmLevelType.getAlarmLevelCode()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,localeMessageSourceService.getMessage("common.create.created.message"));
	}

	@PreAuthorize("hasRole('R_ALARM_ALT_E')")
	@ApiOperation(value = "Edit AlarmLevelType", notes = "")
	@RequestMapping(value = "/{alarmLevelCode}/{alarmTypeCode}", method = RequestMethod.PUT)
	public ResponseRestEntity<AlarmLevelType> updateAlarmLevelType(
			@PathVariable("alarmLevelCode") String alarmLevelCode, @PathVariable("alarmTypeCode") String alarmTypeCode,
			@Valid @RequestBody AlarmLevelType alarmLevelType, BindingResult result) {
		AlarmLevelType bean = new AlarmLevelType();
		bean.setAlarmLevelCode(alarmLevelCode);
		bean.setAlarmTypeCode(alarmTypeCode);
		AlarmLevelType currentAlarmLevelType = alarmLevelTypeService.selectByPrimaryKey(bean);

		if (currentAlarmLevelType == null) {
			return new ResponseRestEntity<AlarmLevelType>(HttpRestStatus.NOT_FOUND, localeMessageSourceService.getMessage("common.update.not_found.message"));
		}

		currentAlarmLevelType.setAlarmLevelCode(alarmLevelType.getAlarmLevelCode());
		currentAlarmLevelType.setAlarmTypeCode(alarmLevelType.getAlarmTypeCode());
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			for (ObjectError error : list) {
				//System.out.println(error.getCode() + "---" + error.getArguments() + "---" + error.getDefaultMessage());
			}

			return new ResponseRestEntity<AlarmLevelType>(currentAlarmLevelType,
					HttpRestStatusFactory.createStatus(list), HttpRestStatusFactory.createStatusMessage(list));
		}
		alarmLevelTypeService.updateByPrimaryKey(currentAlarmLevelType);
		//修改日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentAlarmLevelType,CommonLogImpl.ALARM);
		return new ResponseRestEntity<AlarmLevelType>(currentAlarmLevelType, HttpRestStatus.OK, localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	@PreAuthorize("hasRole('R_ALARM_ALT_E')")
	@ApiOperation(value = "Edit Part AlarmType", notes = "")
	@RequestMapping(value = "/{alarmLevelCode}/{alarmTypeCode}", method = RequestMethod.PATCH)
	public ResponseRestEntity<AlarmLevelType> updateAlarmLevelTypeSelective(@PathVariable("alarmLevelCode") String alarmLevelCode, 
			@PathVariable("alarmTypeCode") String alarmTypeCode,@RequestBody AlarmLevelType alarmLevelType) {
		AlarmLevelType bean =new AlarmLevelType();
		bean.setAlarmLevelCode(alarmLevelCode);
		bean.setAlarmTypeCode(alarmTypeCode);
		AlarmLevelType currentAlarmLevelType = alarmLevelTypeService.selectByPrimaryKey(bean);

		if (currentAlarmLevelType == null) {
			return new ResponseRestEntity<AlarmLevelType>(HttpRestStatus.NOT_FOUND);
		}
		currentAlarmLevelType.setAlarmLevelCode(alarmLevelType.getAlarmLevelCode());
		currentAlarmLevelType.setAlarmTypeCode(alarmLevelType.getAlarmTypeCode());
		alarmLevelTypeService.updateByPrimaryKeySelective(currentAlarmLevelType);
		//修改日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentAlarmLevelType,CommonLogImpl.ALARM);
		return new ResponseRestEntity<AlarmLevelType>(currentAlarmLevelType, HttpRestStatus.OK);
	}
	

	@PreAuthorize("hasRole('R_ALARM_ALT_E')")
	@ApiOperation(value = "Delete AlarmLevelType", notes = "")
	@RequestMapping(value = "/{alarmLevelCode}/{alarmTypeCode}", method = RequestMethod.DELETE)
	public ResponseRestEntity<AlarmLevelType> deleteAlarmLevelType(
			@PathVariable("alarmLevelCode") String alarmLevelCode,
			@PathVariable("alarmTypeCode") String alarmTypeCode) {
		AlarmLevelType bean = new AlarmLevelType();
		bean.setAlarmLevelCode(alarmLevelCode);
		bean.setAlarmTypeCode(alarmTypeCode);
		AlarmLevelType alarmLevelType = alarmLevelTypeService.selectByPrimaryKey(bean);
		if (alarmLevelType == null) {
			return new ResponseRestEntity<AlarmLevelType>(HttpRestStatus.NOT_FOUND);
		}
		alarmLevelTypeService.deleteByPrimaryKey(alarmLevelType);
		//删除日志开始
		AlarmLevelType delBean = new AlarmLevelType();
		delBean.setAlarmLevelCode(alarmLevelCode);
		delBean.setAlarmTypeCode(alarmTypeCode);
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, delBean,CommonLogImpl.ALARM);
		//删除日志结束
		return new ResponseRestEntity<AlarmLevelType>(HttpRestStatus.NO_CONTENT);
	}

}