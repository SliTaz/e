package com.zbensoft.e.payment.api.control;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import com.zbensoft.e.payment.api.service.api.AlarmEmailManageService;
import com.zbensoft.e.payment.api.service.api.AlarmLevelService;
import com.zbensoft.e.payment.api.service.api.AlarmMangerService;
import com.zbensoft.e.payment.db.domain.AlarmEmailManage;
import com.zbensoft.e.payment.db.domain.AlarmLevel;
import com.zbensoft.e.payment.db.domain.AlarmLevelType;
import com.zbensoft.e.payment.db.domain.AlarmManger;
import com.zbensoft.e.payment.db.domain.AlarmType;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/alarmManger")
@RestController
public class AlarmMangerController {
	@Autowired
	AlarmMangerService alarmMangerService;
	@Autowired
	AlarmEmailManageService alarmEmailManageService;
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;
	@Autowired
	AlarmLevelService alarmLevelService;
	//查询通知，支持分页
	@PreAuthorize("hasRole('R_ALARM_AC_Q')")
	@ApiOperation(value = "Query AlarmManger，Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<AlarmManger>> selectPage(
			@RequestParam(required = false) String id,
			@RequestParam(required = false) String name,
			@RequestParam(required = false) String alarmLevelCode,
			@RequestParam(required = false) String alarmEmailManageId,
			@RequestParam(required = false) String firstTimeSec,
			@RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		AlarmManger alarmManger = new AlarmManger();
		alarmManger.setAlarmKey(id);
		alarmManger.setDeleteFlag(0);
		alarmManger.setName(name);
		alarmManger.setAlarmLevelCode(alarmLevelCode);
		alarmManger.setAlarmEmailManageId(alarmEmailManageId);
		alarmManger.setFirstTimeSec(firstTimeSec);
		List<AlarmManger> list = new ArrayList<AlarmManger>();
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = alarmMangerService.selectPage(alarmManger);

		} else {
			list = alarmMangerService.selectPage(alarmManger);
		}

		int count = alarmMangerService.count(alarmManger);
		// 分页 end
		
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<AlarmManger>>(new ArrayList<AlarmManger>(),HttpRestStatus.NOT_FOUND);
		}
		List<AlarmManger> listNew = new ArrayList<AlarmManger>();
		for(AlarmManger bean:list){
			bean.setAlarmOrigin(bean.getAlarmKey());
			AlarmLevel alarmLevel = alarmLevelService.selectByPrimaryKey(bean.getAlarmLevelCode());
			AlarmEmailManage alarmEmailManage = alarmEmailManageService.selectByPrimaryKey(bean.getAlarmEmailManageId());
			if(alarmLevel!=null){
				bean.setAlarmLevelName(alarmLevel.getName());
			}
			if(alarmEmailManage!=null){
				bean.setAlarmEmailManageName(alarmEmailManage.getName());
			}
			
			listNew.add(bean);
		}
		return new ResponseRestEntity<List<AlarmManger>>(listNew, HttpRestStatus.OK,count,count);
	}

	//查询通知
	@PreAuthorize("hasRole('R_ALARM_AC_Q')")
	@ApiOperation(value = "Query AlarmManger", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<AlarmManger> selectByPrimaryKey(@PathVariable("id") String id) {
		AlarmManger alarmManger = alarmMangerService.selectByPrimaryKey(id);
		if (alarmManger == null) {
			return new ResponseRestEntity<AlarmManger>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<AlarmManger>(alarmManger, HttpRestStatus.OK);
	}

	//新增通知
	@PreAuthorize("hasRole('R_ALARM_AC_E')")
	@ApiOperation(value = "Add AlarmManger", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> create(@Valid @RequestBody AlarmManger alarmManger, BindingResult result, UriComponentsBuilder ucBuilder) {

		alarmManger.setDeleteFlag(0);
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}
		AlarmManger bean = alarmMangerService.selectByPrimaryKey(alarmManger.getAlarmKey());
		if (bean !=null) {
			return new ResponseRestEntity<Void>(HttpRestStatus.CONFLICT,localeMessageSourceService.getMessage("common.create.conflict.message"));
		}
		alarmMangerService.insert(alarmManger);
		//新增日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, alarmManger,CommonLogImpl.ALARM);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/alarmManger/{id}").buildAndExpand(alarmManger.getAlarmKey()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,localeMessageSourceService.getMessage("common.create.created.message"));
	}

	//修改通知信息
	@PreAuthorize("hasRole('R_ALARM_AC_E')")
	@ApiOperation(value = "Edit AlarmManger", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<AlarmManger> update(@PathVariable("id") String id, @Valid @RequestBody AlarmManger alarmManger, BindingResult result) {

		AlarmManger currentAlarmManger = alarmMangerService.selectByPrimaryKey(id);

		if (currentAlarmManger == null) {
			return new ResponseRestEntity<AlarmManger>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}

		
		currentAlarmManger.setName(alarmManger.getName());
		currentAlarmManger.setAlarmLevelCode(alarmManger.getAlarmLevelCode());
		currentAlarmManger.setAlarmEmailManageId(alarmManger.getAlarmEmailManageId());
		currentAlarmManger.setFirstTimeSec(alarmManger.getFirstTimeSec());
		currentAlarmManger.setFrequencyTimeSec(alarmManger.getFrequencyTimeSec());
		currentAlarmManger.setHandleClass(alarmManger.getHandleClass());
		currentAlarmManger.setClassParamter(alarmManger.getClassParamter());
		currentAlarmManger.setDeleteFlag(alarmManger.getDeleteFlag());
		currentAlarmManger.setRemark(alarmManger.getRemark());
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<AlarmManger>(currentAlarmManger,HttpRestStatusFactory.createStatus(list),HttpRestStatusFactory.createStatusMessage(list));
		}
		
		alarmMangerService.updateByPrimaryKey(currentAlarmManger);
		//修改日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentAlarmManger,CommonLogImpl.ALARM);
		return new ResponseRestEntity<AlarmManger>(currentAlarmManger, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	//修改部分通知信息
	@PreAuthorize("hasRole('R_ALARM_AC_E')")
	@ApiOperation(value = "Edit Part AlarmManger", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<AlarmManger> updateSelective(@PathVariable("id") String id, @RequestBody AlarmManger alarmManger) {

		AlarmManger currentAlarmManger = alarmMangerService.selectByPrimaryKey(id);

		if (currentAlarmManger == null) {
			return new ResponseRestEntity<AlarmManger>(HttpRestStatus.NOT_FOUND);
		}
		currentAlarmManger.setAlarmKey(id);
		currentAlarmManger.setName(alarmManger.getName());
		currentAlarmManger.setAlarmLevelCode(alarmManger.getAlarmLevelCode());
		currentAlarmManger.setAlarmEmailManageId(alarmManger.getAlarmEmailManageId());
		currentAlarmManger.setFirstTimeSec(alarmManger.getFirstTimeSec());
		currentAlarmManger.setFrequencyTimeSec(alarmManger.getFrequencyTimeSec());
		currentAlarmManger.setHandleClass(alarmManger.getHandleClass());
		currentAlarmManger.setClassParamter(alarmManger.getClassParamter());
		currentAlarmManger.setDeleteFlag(alarmManger.getDeleteFlag());
		currentAlarmManger.setRemark(alarmManger.getRemark());
		alarmMangerService.updateByPrimaryKeySelective(alarmManger);
		//修改日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, alarmManger,CommonLogImpl.ALARM);
		return new ResponseRestEntity<AlarmManger>(currentAlarmManger, HttpRestStatus.OK);
	}

	//删除指定通知
	@PreAuthorize("hasRole('R_ALARM_AC_E')")
	@ApiOperation(value = "Delete AlarmManger", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<AlarmManger> delete(@PathVariable("id") String id) {

		AlarmManger alarmManger = alarmMangerService.selectByPrimaryKey(id);
		if (alarmManger == null) {
			return new ResponseRestEntity<AlarmManger>(HttpRestStatus.NOT_FOUND);
		}
		alarmManger.setDeleteFlag(1);
		alarmMangerService.updateByPrimaryKey(alarmManger);
		//删除日志开始
		AlarmManger delBean = new AlarmManger();
		delBean.setAlarmKey(id);              
		delBean.setDeleteFlag(1);
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, delBean,CommonLogImpl.ALARM);
		//删除日志结束
		return new ResponseRestEntity<AlarmManger>(HttpRestStatus.NO_CONTENT);
	}

}