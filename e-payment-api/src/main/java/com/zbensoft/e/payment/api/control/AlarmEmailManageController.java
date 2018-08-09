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
import com.zbensoft.e.payment.api.service.api.AlarmEmailManageService;
import com.zbensoft.e.payment.db.domain.AlarmEmailManage;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/alarmEmailManage")
@RestController
public class AlarmEmailManageController {

	@Autowired
	AlarmEmailManageService alarmEmailManageService; 
	
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	@PreAuthorize("hasRole('R_ALARM_AE_Q')")
	@ApiOperation(value = "Query alarmEmailManage，Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<AlarmEmailManage>> selectPage(@RequestParam(required = false) String id,
			@RequestParam(required = false) String name,
			@RequestParam(required = false) String recvPersonMail,
			@RequestParam(required = false) String ccPersonMail,
			@RequestParam(required = false) String bccPersonMail,
			@RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		AlarmEmailManage alarmEmailManage=new AlarmEmailManage();
		alarmEmailManage.setAlarmEmailManageId(id);
		alarmEmailManage.setName(name);
		alarmEmailManage.setBccPersonMail(bccPersonMail);
		alarmEmailManage.setCcPersonMail(ccPersonMail);
		alarmEmailManage.setRecvPersonMail(recvPersonMail);
		int count =alarmEmailManageService.count(alarmEmailManage);
		if (count == 0) {
			return new ResponseRestEntity<List<AlarmEmailManage>>(new ArrayList<AlarmEmailManage>(), HttpRestStatus.NOT_FOUND);
		}
		List<AlarmEmailManage> list = null;
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = alarmEmailManageService.selectPage(alarmEmailManage);
			// System.out.println("pageNum:"+pageNum+";pageSize:"+pageSize);
			// System.out.println("list.size:"+list.size());

		} else {
			list = alarmEmailManageService.selectPage(alarmEmailManage);
		}
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<AlarmEmailManage>>(new ArrayList<AlarmEmailManage>(),HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<List<AlarmEmailManage>>(list, HttpRestStatus.OK, count, count);
	}

	@PreAuthorize("hasRole('R_ALARM_AE_Q')")
	@ApiOperation(value = "Query alarmEmailManage", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<AlarmEmailManage> selectByPrimaryKey(@PathVariable("id") String id) {
		AlarmEmailManage alarmEmailManage = alarmEmailManageService.selectByPrimaryKey(id);
		if (alarmEmailManage == null) {
			return new ResponseRestEntity<AlarmEmailManage>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<AlarmEmailManage>(alarmEmailManage, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_ALARM_AE_E')")
	@ApiOperation(value = "Add alarmEmailManage", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createAlarmEmailManage(@Valid @RequestBody AlarmEmailManage alarmEmailManage,BindingResult result, UriComponentsBuilder ucBuilder) {
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}
		AlarmEmailManage bean = alarmEmailManageService.selectByPrimaryKey(alarmEmailManage.getAlarmEmailManageId());
		if (bean !=null) {
			return new ResponseRestEntity<Void>(HttpRestStatus.CONFLICT,localeMessageSourceService.getMessage("common.create.conflict.message"));
		}
		alarmEmailManageService.insert(alarmEmailManage);
		//新增日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, alarmEmailManage,CommonLogImpl.ALARM);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/alarmType/{id}").buildAndExpand(alarmEmailManage.getAlarmEmailManageId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,localeMessageSourceService.getMessage("common.create.created.message"));
	}

	@PreAuthorize("hasRole('R_ALARM_AE_E')")
	@ApiOperation(value = "Edit AlarmEmailManage", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<AlarmEmailManage> updateAlarmEmailManage(@PathVariable("id") String id,@Valid @RequestBody AlarmEmailManage alarmEmailManage, BindingResult result) {

		AlarmEmailManage currentAlarmEmailManage = alarmEmailManageService.selectByPrimaryKey(id);

		if (currentAlarmEmailManage == null) {
			return new ResponseRestEntity<AlarmEmailManage>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}

		currentAlarmEmailManage.setBccPersonMail(alarmEmailManage.getBccPersonMail());
		currentAlarmEmailManage.setCcPersonMail(alarmEmailManage.getCcPersonMail());
		currentAlarmEmailManage.setName(alarmEmailManage.getName());
		currentAlarmEmailManage.setRecvPersonMail(alarmEmailManage.getRecvPersonMail());
		currentAlarmEmailManage.setRemark(alarmEmailManage.getRemark());
		
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			for (ObjectError error : list) {
				//System.out.println(error.getCode() + "---" + error.getArguments() + "---" + error.getDefaultMessage());
			}

			return new ResponseRestEntity<AlarmEmailManage>(currentAlarmEmailManage,HttpRestStatusFactory.createStatus(list),HttpRestStatusFactory.createStatusMessage(list));
		}
		alarmEmailManageService.updateByPrimaryKey(currentAlarmEmailManage);
		//修改日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentAlarmEmailManage,CommonLogImpl.ALARM);
		return new ResponseRestEntity<AlarmEmailManage>(currentAlarmEmailManage, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	@PreAuthorize("hasRole('R_ALARM_AE_E')")
	@ApiOperation(value = "Edit Part AlarmEmailManage", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<AlarmEmailManage> updateAlarmEmailManageSelective(@PathVariable("id") String id,
			@RequestBody AlarmEmailManage alarmEmailManage) {

		AlarmEmailManage currentAlarmEmailManage = alarmEmailManageService.selectByPrimaryKey(id);

		if (currentAlarmEmailManage == null) {
			return new ResponseRestEntity<AlarmEmailManage>(HttpRestStatus.NOT_FOUND);
		}
		currentAlarmEmailManage.setAlarmEmailManageId(id);;
		currentAlarmEmailManage.setBccPersonMail(alarmEmailManage.getBccPersonMail());
		currentAlarmEmailManage.setCcPersonMail(alarmEmailManage.getCcPersonMail());
		currentAlarmEmailManage.setName(alarmEmailManage.getName());
		currentAlarmEmailManage.setRecvPersonMail(alarmEmailManage.getRecvPersonMail());
		currentAlarmEmailManage.setRemark(alarmEmailManage.getRemark());
		alarmEmailManageService.updateByPrimaryKeySelective(currentAlarmEmailManage);
		//修改日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentAlarmEmailManage,CommonLogImpl.ALARM);
		return new ResponseRestEntity<AlarmEmailManage>(currentAlarmEmailManage, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_ALARM_AE_E')")
	@ApiOperation(value = "Delete AlarmEmailManage", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<AlarmEmailManage> deleteAlarmEmailManage(@PathVariable("id") String id) {

		AlarmEmailManage alarmEmailManage = alarmEmailManageService.selectByPrimaryKey(id);
		if (alarmEmailManage == null) {
			return new ResponseRestEntity<AlarmEmailManage>(HttpRestStatus.NOT_FOUND);
		}
		alarmEmailManageService.deleteByPrimaryKey(id);
		//删除日志开始
		AlarmEmailManage delBean = new AlarmEmailManage();
		delBean.setAlarmEmailManageId(id);;
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, delBean,CommonLogImpl.ALARM);
		//删除日志结束
		return new ResponseRestEntity<AlarmEmailManage>(HttpRestStatus.NO_CONTENT);
	}
}
