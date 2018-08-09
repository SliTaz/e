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
import com.zbensoft.e.payment.db.domain.AlarmLevel;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/alarmLevel")
@RestController
public class AlarmLevelController {
	@Autowired
	AlarmLevelService alarmLevelService;
	
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	@PreAuthorize("hasRole('R_ALARM_AL_Q')")
	@ApiOperation(value = "Query AlarmLevel，Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<AlarmLevel>> selectPage(@RequestParam(required = false) String id,
			@RequestParam(required = false) String name,
			@RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		AlarmLevel alarmLevel = new AlarmLevel();
		alarmLevel.setAlarmLevelCode(id);
		alarmLevel.setName(name);
		int count = alarmLevelService.count(alarmLevel);
		if (count == 0) {
			return new ResponseRestEntity<List<AlarmLevel>>(new ArrayList<AlarmLevel>(), HttpRestStatus.NOT_FOUND);
		}
		List<AlarmLevel> list = null;
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = alarmLevelService.selectPage(alarmLevel);
			// System.out.println("pageNum:"+pageNum+";pageSize:"+pageSize);
			// System.out.println("list.size:"+list.size());

		} else {
			list = alarmLevelService.selectPage(alarmLevel);
		}
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<AlarmLevel>>(new ArrayList<AlarmLevel>(),HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<List<AlarmLevel>>(list, HttpRestStatus.OK, count, count);
	}

	@PreAuthorize("hasRole('R_ALARM_AL_Q')")
	@ApiOperation(value = "Query AlarmLevel", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<AlarmLevel> selectByPrimaryKey(@PathVariable("id") String id) {
		AlarmLevel alarmLevel = alarmLevelService.selectByPrimaryKey(id);
		if (alarmLevel == null) {
			return new ResponseRestEntity<AlarmLevel>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<AlarmLevel>(alarmLevel, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_ALARM_AL_E')")
	@ApiOperation(value = "Add AlarmLevel", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createAlarmLevel(@Valid @RequestBody AlarmLevel alarmLevel,BindingResult result, UriComponentsBuilder ucBuilder) {
		
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}
		AlarmLevel bean = alarmLevelService.selectByPrimaryKey(alarmLevel.getAlarmLevelCode());
		if (bean !=null) {
			return new ResponseRestEntity<Void>(HttpRestStatus.CONFLICT,localeMessageSourceService.getMessage("common.create.conflict.message"));
		}
		alarmLevelService.insert(alarmLevel);
		//新增日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, alarmLevel,CommonLogImpl.ALARM);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/alarmLevel/{id}").buildAndExpand(alarmLevel.getAlarmLevelCode()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,localeMessageSourceService.getMessage("common.create.created.message"));
	}

	@PreAuthorize("hasRole('R_ALARM_AL_E')")
	@ApiOperation(value = "Edit AlarmLevel", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<AlarmLevel> updateAlarmLevel(@PathVariable("id") String id,@Valid @RequestBody AlarmLevel alarmLevel, BindingResult result) {

		AlarmLevel currentAlarmLevel = alarmLevelService.selectByPrimaryKey(id);

		if (currentAlarmLevel == null) {
			return new ResponseRestEntity<AlarmLevel>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}

		currentAlarmLevel.setName(alarmLevel.getName());
		currentAlarmLevel.setRemark(alarmLevel.getRemark());
		
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			for (ObjectError error : list) {
				//System.out.println(error.getCode() + "---" + error.getArguments() + "---" + error.getDefaultMessage());
			}

			return new ResponseRestEntity<AlarmLevel>(currentAlarmLevel,HttpRestStatusFactory.createStatus(list),HttpRestStatusFactory.createStatusMessage(list));
		}
		alarmLevelService.updateByPrimaryKey(currentAlarmLevel);
		//修改日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentAlarmLevel,CommonLogImpl.ALARM);
		return new ResponseRestEntity<AlarmLevel>(currentAlarmLevel, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	@PreAuthorize("hasRole('R_ALARM_AL_E')")
	@ApiOperation(value = "Edit Part AlarmLevel", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<AlarmLevel> updateAlarmLevelSelective(@PathVariable("id") String id,
			@RequestBody AlarmLevel alarmLevel) {

		AlarmLevel currentAlarmLevel = alarmLevelService.selectByPrimaryKey(id);

		if (currentAlarmLevel == null) {
			return new ResponseRestEntity<AlarmLevel>(HttpRestStatus.NOT_FOUND);
		}
		alarmLevel.setAlarmLevelCode(id);
		alarmLevelService.updateByPrimaryKeySelective(alarmLevel);
		//修改日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, alarmLevel,CommonLogImpl.ALARM);
		return new ResponseRestEntity<AlarmLevel>(currentAlarmLevel, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_ALARM_AL_E')")
	@ApiOperation(value = "Delete AlarmLevel", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<AlarmLevel> deleteAlarmLevel(@PathVariable("id") String id) {

		AlarmLevel alarmLevel = alarmLevelService.selectByPrimaryKey(id);
		if (alarmLevel == null) {
			return new ResponseRestEntity<AlarmLevel>(HttpRestStatus.NOT_FOUND);
		}

		alarmLevelService.deleteByPrimaryKey(id);
		//删除日志开始
		AlarmLevel delBean = new AlarmLevel();
		delBean.setAlarmLevelCode(id);              
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, delBean,CommonLogImpl.ALARM);
		//删除日志结束
		return new ResponseRestEntity<AlarmLevel>(HttpRestStatus.NO_CONTENT);
	}

}