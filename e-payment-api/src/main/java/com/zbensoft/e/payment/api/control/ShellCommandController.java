package com.zbensoft.e.payment.api.control;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

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
import com.zbensoft.e.payment.api.common.CommonFun;
import com.zbensoft.e.payment.api.common.CommonLogImpl;
import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.HttpRestStatusFactory;
import com.zbensoft.e.payment.api.common.LocaleMessageSourceService;
import com.zbensoft.e.payment.api.common.PageHelperUtil;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.service.api.ShellCommandService;
import com.zbensoft.e.payment.api.service.api.ShellPcService;
import com.zbensoft.e.payment.db.domain.ShellCommand;
import com.zbensoft.e.payment.db.domain.ShellPc;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/shellCommand")
@RestController
public class ShellCommandController {
	@Autowired
	ShellCommandService shellCommandService;
	@Autowired
	ShellPcService shellPcService;
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	@PreAuthorize("hasRole('R_MAINTEN_SM_Q')")
	@ApiOperation(value = "Query ShellCommand,Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<ShellCommand>> selectPage(@RequestParam(required = false) String shellCode,
			@RequestParam(required = false) String pcCode, @RequestParam(required = false) String name,
			@RequestParam(required = false) Integer type, @RequestParam(required = false) String remark ,
			@RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		ShellCommand shellCommand = new ShellCommand();
		shellCommand.setShellCode(shellCode);
		shellCommand.setPcCode(pcCode);
		shellCommand.setName(name);
		shellCommand.setType(type);
		shellCommand.setRemark(remark);

		int count = shellCommandService.count(shellCommand);
		if (count == 0) {
			return new ResponseRestEntity<List<ShellCommand>>(new ArrayList<ShellCommand>(), HttpRestStatus.NOT_FOUND);
		}

		List<ShellCommand> list = null;// consumerUserService.selectPage(consumerUser);

		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			// 第一个参数是第几页；第二个参数是每页显示条数。
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = shellCommandService.selectPage(shellCommand);
		} else {
			list = shellCommandService.selectPage(shellCommand);
		}
		List<ShellCommand> listNew = new ArrayList<ShellCommand>();
		for(ShellCommand bean:list){
			bean.setShellCode(bean.getShellCode());
			ShellPc shellPc = shellPcService.selectByPrimaryKey(bean.getPcCode());
			if(shellPc!=null){
				bean.setPcName(shellPc.getName());
			}
			listNew.add(bean);
		}
		return new ResponseRestEntity<List<ShellCommand>>(listNew, HttpRestStatus.OK, count, count);
	}
	//shell查询
	@PreAuthorize("hasRole('R_MAINTEN_SM_Q')")
	@ApiOperation(value = "Query ShellCommand,Support paging", notes = "")
	@RequestMapping(value = "/detail/{shellCode}", method = RequestMethod.GET)
	public ResponseRestEntity<List<ShellCommand>> selectPageDetail(@PathVariable("shellCode") String shellCode,
			@RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		ShellCommand shellCommand = new ShellCommand();
		shellCommand.setShellCode(shellCode);
		// 必须输入一个进行查询
		if ((shellCode == null || "".equals(shellCode)) ) {
			return new ResponseRestEntity<List<ShellCommand>>(new ArrayList<ShellCommand>(), HttpRestStatus.NOT_FOUND);
		}
		int count = shellCommandService.count(shellCommand);
		if (count == 0) {
			return new ResponseRestEntity<List<ShellCommand>>(new ArrayList<ShellCommand>(), HttpRestStatus.NOT_FOUND);
		}

		List<ShellCommand> list = null;// consumerUserService.selectPage(consumerUser);

		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			// 第一个参数是第几页；第二个参数是每页显示条数。
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = shellCommandService.selectPage(shellCommand);
		} else {
			list = shellCommandService.selectPage(shellCommand);
		}
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<ShellCommand>>(new ArrayList<ShellCommand>(),
					HttpRestStatus.NOT_FOUND);
		}
		List<ShellCommand> listNew = new ArrayList<ShellCommand>();
		for(ShellCommand bean:list){
			ShellPc shellPc = shellPcService.selectByPrimaryKey(bean.getPcCode());
			if(shellPc!=null){
				bean.setDetail(bean.getCommand()+" "+shellPc.getUserName()+" "+"##"+shellPc.getPcCode()+"##"+" "+bean.getParam());
			}
			else{
				bean.setDetail(bean.getCommand()+" "+bean.getParam());
			}
			listNew.add(bean);
		}
		return new ResponseRestEntity<List<ShellCommand>>(listNew, HttpRestStatus.OK, count, count);
	}
	//shell执行结果
	@PreAuthorize("hasRole('R_MAINTEN_S_E')")
	@ApiOperation(value = "Query ShellCommand,Support paging", notes = "")
	@RequestMapping(value = "/runResult", method = RequestMethod.PUT)
	public ResponseRestEntity<ShellCommand> selectPageRun(@RequestBody ShellCommand shellCommand
		) {
/*		if(!"".equals(shellCommand.getShellCode())&&shellCommand.getShellCode()!=null){
		shellCommand = shellCommandService.selectByPrimaryKey(shellCommand.getShellCode());
			shellCommand.setDetail(shellCommand.getCommand()+" "+shellCommand.getParam());
		}*/
		ShellCommand shell=new ShellCommand();
		shell.setContent(CommonFun.runScript(shellCommand.getDetail()));
		return new ResponseRestEntity<ShellCommand>(shell, HttpRestStatus.OK);
	}
	// 查询应用
	@PreAuthorize("hasRole('R_MAINTEN_SM_Q')")
	@ApiOperation(value = "Query ShellCommand", notes = "")
	@RequestMapping(value = "/{ShellCommand}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<ShellCommand> selectByPrimaryKey(@PathVariable("shellCode") String shellCode) {
		ShellCommand shellCommand = shellCommandService.selectByPrimaryKey(shellCode);
		if (shellCommand == null) {
			return new ResponseRestEntity<ShellCommand>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<ShellCommand>(shellCommand, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_MAINTEN_SM_E')")
	@ApiOperation(value = "Add ShellCommand", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createShellCommand(@RequestBody ShellCommand shellCommand,BindingResult result,  UriComponentsBuilder ucBuilder) {
		// 校验
				if (result.hasErrors()) {
					List<ObjectError> list = result.getAllErrors();
					return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
							HttpRestStatusFactory.createStatusMessage(list));
				}
		if (shellCommandService.isExist(shellCommand)) {
			return new ResponseRestEntity<Void>(HttpRestStatus.CONFLICT,localeMessageSourceService.getMessage("common.create.conflict.message"));
		}

		shellCommandService.insert(shellCommand);
		//新增日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, shellCommand,CommonLogImpl.SYSTEM_MANAGE);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/shellCommand/{shellCode}").buildAndExpand(shellCommand.getShellCode()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,localeMessageSourceService.getMessage("common.create.created.message"));
	}

	@PreAuthorize("hasRole('R_MAINTEN_SM_E')")
	@ApiOperation(value = "Edit shellCode", notes = "")
	@RequestMapping(value = "{shellCode}", method = RequestMethod.PUT)
	public ResponseRestEntity<ShellCommand> updaShellCommand(@PathVariable("shellCode") String shellCode, @RequestBody ShellCommand shellCommand) {

		ShellCommand type = shellCommandService.selectByPrimaryKey(shellCode);

		if (type == null) {
			return new ResponseRestEntity<ShellCommand>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}
		type.setShellCode(shellCommand.getShellCode());
		type.setPcCode(shellCommand.getPcCode());
		type.setName(shellCommand.getName());
		type.setType(shellCommand.getType());
		type.setCommand(shellCommand.getCommand());
		type.setParam(shellCommand.getParam());
		type.setRemark(shellCommand.getRemark());
		
		shellCommandService.updateByPrimaryKey(type);
		//修改日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, type,CommonLogImpl.SYSTEM_MANAGE);
		return new ResponseRestEntity<ShellCommand>(type, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	@PreAuthorize("hasRole('R_MAINTEN_SM_E')")
	@ApiOperation(value = "Edit Part TaskType", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<ShellCommand> updateShellCommandSelective(@PathVariable("shellCode") String shellCode, @RequestBody ShellCommand shellCommand) {

		ShellCommand type = shellCommandService.selectByPrimaryKey(shellCode);

		if (type == null) {
			return new ResponseRestEntity<ShellCommand>(HttpRestStatus.NOT_FOUND);
		}
		shellCommandService.updateByPrimaryKeySelective(shellCommand);
		//修改日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, shellCommand,CommonLogImpl.SYSTEM_MANAGE);
		return new ResponseRestEntity<ShellCommand>(type, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_MAINTEN_SM_E')")
	@ApiOperation(value = "Delete shellCommand", notes = "")
	@RequestMapping(value = "/{shellCode}", method = RequestMethod.DELETE)
	public ResponseRestEntity<ShellCommand> deleteShellCommand(@PathVariable("shellCode") String shellCode) {

		ShellCommand shellCommand = shellCommandService.selectByPrimaryKey(shellCode);
		if (shellCommand == null) {
			return new ResponseRestEntity<ShellCommand>(HttpRestStatus.NOT_FOUND);
		}

		shellCommandService.deleteByPrimaryKey(shellCode);
		//删除日志开始
		ShellCommand delBean = new ShellCommand();
		delBean.setShellCode(shellCode);              

		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, delBean,CommonLogImpl.SYSTEM_MANAGE);
		//删除日志结束
		return new ResponseRestEntity<ShellCommand>(HttpRestStatus.NO_CONTENT);
	}

}