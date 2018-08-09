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
import com.zbensoft.e.payment.api.service.api.AlarmTypeService;
import com.zbensoft.e.payment.db.domain.AlarmLevel;
import com.zbensoft.e.payment.db.domain.AlarmType;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/alarmType")
@RestController
public class AlarmTypeController {
	@Autowired
	AlarmTypeService alarmTypeService;
	
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	@PreAuthorize("hasRole('R_ALARM_AT_Q')")
	@ApiOperation(value = "Query AlarmType，Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<AlarmType>> selectPage(@RequestParam(required = false) String id,
			@RequestParam(required = false) String name,
			@RequestParam(required = false) String handleClass,
			@RequestParam(required = false) String classParamter,
			@RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		AlarmType alarmType = new AlarmType();
		alarmType.setAlarmTypeCode(id);
		alarmType.setName(name);
		alarmType.setHandleClass(handleClass);
		alarmType.setClassParamter(classParamter);
		alarmType.setDeleteFlag(0);
		int count = alarmTypeService.count(alarmType);
		if (count == 0) {
			return new ResponseRestEntity<List<AlarmType>>(new ArrayList<AlarmType>(), HttpRestStatus.NOT_FOUND);
		}
		List<AlarmType> list = null;
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = alarmTypeService.selectPage(alarmType);
			// System.out.println("pageNum:"+pageNum+";pageSize:"+pageSize);
			// System.out.println("list.size:"+list.size());

		} else {
			list = alarmTypeService.selectPage(alarmType);
		}
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<AlarmType>>(new ArrayList<AlarmType>(),HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<List<AlarmType>>(list, HttpRestStatus.OK, count, count);
	}

	@PreAuthorize("hasRole('R_ALARM_AT_Q')")
	@ApiOperation(value = "Query AlarmType", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<AlarmType> selectByPrimaryKey(@PathVariable("id") String id) {
		AlarmType alarmType = alarmTypeService.selectByPrimaryKey(id);
		if (alarmType == null) {
			return new ResponseRestEntity<AlarmType>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<AlarmType>(alarmType, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_ALARM_AT_E')")
	@ApiOperation(value = "Add AlarmType", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createAlarmType(@Valid @RequestBody AlarmType alarmType,BindingResult result, UriComponentsBuilder ucBuilder) {
		alarmType.setDeleteFlag(0);
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}
		AlarmType bean = alarmTypeService.selectByPrimaryKey(alarmType.getAlarmTypeCode());
		if (bean !=null) {
			return new ResponseRestEntity<Void>(HttpRestStatus.CONFLICT,localeMessageSourceService.getMessage("common.create.conflict.message"));
		}
		alarmTypeService.insert(alarmType);
		//新增日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, alarmType,CommonLogImpl.ALARM);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/alarmType/{id}").buildAndExpand(alarmType.getAlarmTypeCode()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,localeMessageSourceService.getMessage("common.create.created.message"));
	}

	@PreAuthorize("hasRole('R_ALARM_AT_E')")
	@ApiOperation(value = "Edit AlarmType", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<AlarmType> updateAlarmType(@PathVariable("id") String id,@Valid @RequestBody AlarmType alarmType, BindingResult result) {

		AlarmType currentAlarmType = alarmTypeService.selectByPrimaryKey(id);

		if (currentAlarmType == null) {
			return new ResponseRestEntity<AlarmType>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}

		currentAlarmType.setName(alarmType.getName());
		currentAlarmType.setHandleClass(alarmType.getHandleClass());
		currentAlarmType.setClassParamter(alarmType.getClassParamter());
		currentAlarmType.setDeleteFlag(alarmType.getDeleteFlag());
		currentAlarmType.setRemark(alarmType.getRemark());
		
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			for (ObjectError error : list) {
				//System.out.println(error.getCode() + "---" + error.getArguments() + "---" + error.getDefaultMessage());
			}

			return new ResponseRestEntity<AlarmType>(currentAlarmType,HttpRestStatusFactory.createStatus(list),HttpRestStatusFactory.createStatusMessage(list));
		}
		alarmTypeService.updateByPrimaryKey(currentAlarmType);
		//修改日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentAlarmType,CommonLogImpl.ALARM);
		return new ResponseRestEntity<AlarmType>(currentAlarmType, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	@PreAuthorize("hasRole('R_ALARM_AT_E')")
	@ApiOperation(value = "Edit Part AlarmType", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<AlarmType> updateAlarmTypeSelective(@PathVariable("id") String id,
			@RequestBody AlarmType alarmType) {

		AlarmType currentAlarmType = alarmTypeService.selectByPrimaryKey(id);

		if (currentAlarmType == null) {
			return new ResponseRestEntity<AlarmType>(HttpRestStatus.NOT_FOUND);
		}
		currentAlarmType.setAlarmTypeCode(id);
		currentAlarmType.setName(alarmType.getName());
		currentAlarmType.setHandleClass(alarmType.getHandleClass());
		currentAlarmType.setClassParamter(alarmType.getClassParamter());
		currentAlarmType.setDeleteFlag(alarmType.getDeleteFlag());
		currentAlarmType.setRemark(alarmType.getRemark());
		alarmTypeService.updateByPrimaryKeySelective(currentAlarmType);
		//修改日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentAlarmType,CommonLogImpl.ALARM);
		return new ResponseRestEntity<AlarmType>(currentAlarmType, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_ALARM_AT_E')")
	@ApiOperation(value = "Delete AlarmType", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<AlarmType> deleteAlarmType(@PathVariable("id") String id) {

		AlarmType alarmType = alarmTypeService.selectByPrimaryKey(id);
		if (alarmType == null) {
			return new ResponseRestEntity<AlarmType>(HttpRestStatus.NOT_FOUND);
		}
		alarmType.setDeleteFlag(1);
		alarmTypeService.updateByPrimaryKey(alarmType);
		//删除日志开始
		AlarmType delBean = new AlarmType();
		delBean.setAlarmTypeCode(id);              
		delBean.setDeleteFlag(1);
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, delBean,CommonLogImpl.ALARM);
		//删除日志结束
		return new ResponseRestEntity<AlarmType>(HttpRestStatus.NO_CONTENT);
	}

}