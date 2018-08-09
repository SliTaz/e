package com.zbensoft.e.payment.api.control;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.HttpRestStatusFactory;
import com.zbensoft.e.payment.api.common.IDGenerate;
import com.zbensoft.e.payment.api.common.LocaleMessageSourceService;
import com.zbensoft.e.payment.api.common.PageHelperUtil;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.service.api.GovUserService;
import com.zbensoft.e.payment.api.service.api.SysLogService;
import com.zbensoft.e.payment.db.domain.GovUser;
import com.zbensoft.e.payment.db.domain.SysLog;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/sysLog")
@RestController
public class SysLogController {
	@Autowired
	SysLogService sysLogService;
	
	@Autowired
	GovUserService govUserService;
	
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	@PreAuthorize("hasRole('R_SYSTEM_L_Q')")
	@ApiOperation(value = "Query SysLog，Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<SysLog>> selectPage(@RequestParam(required = false) String id,
			@RequestParam(required = false) String operUser,
			@RequestParam(required = false) Integer operModel,
			@RequestParam(required = false) Integer operType,
			@RequestParam(required = false) String operateTimeStart,
			@RequestParam(required = false) String operateTimeEnd,
			@RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		SysLog sysLog = new SysLog();
		sysLog.setSysLogId(id);
		sysLog.setOperUser(operUser);
		sysLog.setOperModel(operModel);
		sysLog.setOperType(operType);
		sysLog.setOperateTimeStart(operateTimeStart);
		sysLog.setOperateTimeEnd(operateTimeEnd);
		
		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String loginName = userDetails.getUsername();
		GovUser govUser = govUserService.selectByUserName(loginName);
		//登录用户为gov用户
		if(govUser!=null){
			sysLog.setOperUser(loginName);
		}
		
		int count = sysLogService.count(sysLog);
		if (count == 0) {
			return new ResponseRestEntity<List<SysLog>>(new ArrayList<SysLog>(), HttpRestStatus.NOT_FOUND);
		}
		List<SysLog> list = null;
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = sysLogService.selectPage(sysLog);
			// System.out.println("pageNum:"+pageNum+";pageSize:"+pageSize);
			// System.out.println("list.size:"+list.size());

		} else {
			list = sysLogService.selectPage(sysLog);
		}
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<SysLog>>(new ArrayList<SysLog>(),HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<List<SysLog>>(list, HttpRestStatus.OK, count, count);
	}

	@PreAuthorize("hasRole('R_SYSTEM_L_Q')")
	@ApiOperation(value = "Query SysLog", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<SysLog> selectByPrimaryKey(@PathVariable("id") String id) {
		SysLog sysLog = sysLogService.selectByPrimaryKey(id);
		if (sysLog == null) {
			return new ResponseRestEntity<SysLog>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<SysLog>(sysLog, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_SYSTEM_L_E')")
	@ApiOperation(value = "Add SysLog", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createSysLog(@Valid @RequestBody SysLog sysLog,BindingResult result, UriComponentsBuilder ucBuilder) {
		sysLog.setSysLogId(IDGenerate.generateCommOne(IDGenerate.SYS_LOG));
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}
		sysLogService.insert(sysLog);

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/sysLog/{id}").buildAndExpand(sysLog.getSysLogId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,localeMessageSourceService.getMessage("common.create.created.message"));
	}

	@PreAuthorize("hasRole('R_SYSTEM_L_E')")
	@ApiOperation(value = "Edit SysLog", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<SysLog> updateSysLog(@PathVariable("id") String id,@Valid @RequestBody SysLog sysLog, BindingResult result) {

		SysLog currentSysLog = sysLogService.selectByPrimaryKey(id);

		if (currentSysLog == null) {
			return new ResponseRestEntity<SysLog>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}

		currentSysLog.setOperTime(sysLog.getOperTime());
		currentSysLog.setOperUser(sysLog.getOperUser());
		currentSysLog.setOperModel(sysLog.getOperModel());
		currentSysLog.setOperType(sysLog.getOperType());
		currentSysLog.setOperContent(sysLog.getOperContent());
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			for (ObjectError error : list) {
				//System.out.println(error.getCode() + "---" + error.getArguments() + "---" + error.getDefaultMessage());
			}

			return new ResponseRestEntity<SysLog>(currentSysLog,HttpRestStatusFactory.createStatus(list),HttpRestStatusFactory.createStatusMessage(list));
		}
		sysLogService.updateByPrimaryKey(currentSysLog);

		return new ResponseRestEntity<SysLog>(currentSysLog, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	@PreAuthorize("hasRole('R_SYSTEM_L_E')")
	@ApiOperation(value = "Edit Part SysLog", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<SysLog> updateSysLogSelective(@PathVariable("id") String id,
			@RequestBody SysLog sysLog) {

		SysLog currentSysLog = sysLogService.selectByPrimaryKey(id);

		if (currentSysLog == null) {
			return new ResponseRestEntity<SysLog>(HttpRestStatus.NOT_FOUND);
		}
		currentSysLog.setSysLogId(id);
		currentSysLog.setOperTime(sysLog.getOperTime());
		currentSysLog.setOperUser(sysLog.getOperUser());
		currentSysLog.setOperModel(sysLog.getOperModel());
		currentSysLog.setOperType(sysLog.getOperType());
		currentSysLog.setOperContent(sysLog.getOperContent());
		sysLogService.updateByPrimaryKeySelective(currentSysLog);

		return new ResponseRestEntity<SysLog>(currentSysLog, HttpRestStatus.OK);
	}
	
	//批量
	@PreAuthorize("hasRole('R_SYSTEM_L_E')")
	@ApiOperation(value = "Delete Many SysLogs", notes = "")
	@RequestMapping(value = "/deleteMany/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<SysLog> deleteSysLogMany(@PathVariable("id") String id) {
        String[] idStr = id.split(",");
        if(idStr!=null){
        	for(String str :idStr){
        	  sysLogService.deleteByPrimaryKey(str);
        	}
        }
		return new ResponseRestEntity<SysLog>(HttpRestStatus.NO_CONTENT);
	}
	

	@PreAuthorize("hasRole('R_SYSTEM_L_E')")
	@ApiOperation(value = "Delete SysLog", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<SysLog> deleteSysLog(@PathVariable("id") String id) {

		SysLog sysLog = sysLogService.selectByPrimaryKey(id);
		if (sysLog == null) {
			return new ResponseRestEntity<SysLog>(HttpRestStatus.NOT_FOUND);
		}

		sysLogService.deleteByPrimaryKey(id);
		return new ResponseRestEntity<SysLog>(HttpRestStatus.NO_CONTENT);
	}

}