package com.zbensoft.e.payment.api.control;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.zbensoft.e.payment.api.common.CryptHelper;
import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.HttpRestStatusFactory;
import com.zbensoft.e.payment.api.common.IDGenerate;
import com.zbensoft.e.payment.api.common.LocaleMessageSourceService;
import com.zbensoft.e.payment.api.common.PageHelperUtil;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.service.api.ShellPcService;
import com.zbensoft.e.payment.db.domain.ShellPc;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/shellPc")
@RestController
public class ShellPcController {
	
	private static final Logger log = LoggerFactory.getLogger(ShellPcController.class);

	@Autowired
	ShellPcService shellPcService;
	
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	//查询支付应用，支持分页
	@PreAuthorize("hasRole('R_MAINTEN_SS_Q')")
	@ApiOperation(value = "Query ShellPc，Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<ShellPc>> selectPage(
			@RequestParam(required = false) String id,
			@RequestParam(required = false) String name,
			@RequestParam(required = false) String userName,
			@RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		ShellPc shellPc = new ShellPc();
		shellPc.setPcCode(id);
		shellPc.setName(name);
		shellPc.setUserName(userName);
	
		List<ShellPc> list = new ArrayList<ShellPc>();
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = shellPcService.selectPage(shellPc);

		} else {
			list = shellPcService.selectPage(shellPc);
		}

		int count = shellPcService.count(shellPc);
		// 分页 end
		
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<ShellPc>>(new ArrayList<ShellPc>(),HttpRestStatus.NOT_FOUND);
		}
		List<ShellPc> listNew = new ArrayList<ShellPc>();
		for(ShellPc bean:list){
			
			//解密
			try {
			String JieMI = CryptHelper.decrypt(bean.getPassword());
			bean.setPassword(JieMI);
			} catch (Exception e) {
				log.error("",e);
			}
			
			listNew.add(bean);
		}
		return new ResponseRestEntity<List<ShellPc>>(list, HttpRestStatus.OK,count,count);
	}

	//查询支付应用
	@PreAuthorize("hasRole('R_MAINTEN_SS_Q')")
	@ApiOperation(value = "Query ShellPc", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<ShellPc> selectByPrimaryKey(@PathVariable("id") String id) {
		ShellPc shellPc = shellPcService.selectByPrimaryKey(id);
		if (shellPc == null) {
			return new ResponseRestEntity<ShellPc>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<ShellPc>(shellPc, HttpRestStatus.OK);
	}

	//新增支付应用
	@PreAuthorize("hasRole('R_MAINTEN_SS_E')")
	@ApiOperation(value = "Add ShellPc", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> create(@Valid @RequestBody ShellPc shellPc, BindingResult result, UriComponentsBuilder ucBuilder) {
		shellPc.setPcCode(IDGenerate.generateCommOne(IDGenerate.SHELL_PC));

		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}
		//加密
		try {
		String jiaMi = CryptHelper.encrypt(shellPc.getPassword());
		shellPc.setPassword(jiaMi);
		} catch (Exception e) {
			log.error("",e);
		}
		shellPcService.insert(shellPc);
		shellPc.setPassword("");
		//新增日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, shellPc,CommonLogImpl.SYSTEM_MANAGE);

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/shellPc/{id}").buildAndExpand(shellPc.getPcCode()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,localeMessageSourceService.getMessage("common.create.created.message"));
	}

	//修改支付应用信息
	@PreAuthorize("hasRole('R_MAINTEN_SS_E')")
	@ApiOperation(value = "Edit ShellPc", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<ShellPc> update(@PathVariable("id") String id, @Valid @RequestBody ShellPc shellPc, BindingResult result) {

		ShellPc currentShellPc = shellPcService.selectByPrimaryKey(id);

		if (currentShellPc == null) {
			return new ResponseRestEntity<ShellPc>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}

		
		
		currentShellPc.setName(shellPc.getName());
		currentShellPc.setUserName(shellPc.getUserName());
		currentShellPc.setIpAddr(shellPc.getIpAddr());
	
		//加密
		try {
		String jiaMi = CryptHelper.encrypt(shellPc.getPassword());
		currentShellPc.setPassword(jiaMi);
		} catch (Exception e) {
			log.error("",e);
		}
		currentShellPc.setRemark(shellPc.getRemark());
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<ShellPc>(currentShellPc,HttpRestStatusFactory.createStatus(list),HttpRestStatusFactory.createStatusMessage(list));
		}
		
		shellPcService.updateByPrimaryKey(currentShellPc);
		
		currentShellPc.setPassword("");
		//修改日志
       CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentShellPc,CommonLogImpl.SYSTEM_MANAGE);
		return new ResponseRestEntity<ShellPc>(currentShellPc, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	//修改部分支付应用信息
	@PreAuthorize("hasRole('R_MAINTEN_SS_E')")
	@ApiOperation(value = "Edit Part ShellPc", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<ShellPc> updateSelective(@PathVariable("id") String id, @RequestBody ShellPc shellPc) {

		ShellPc currentShellPc = shellPcService.selectByPrimaryKey(id);

		if (currentShellPc == null) {
			return new ResponseRestEntity<ShellPc>(HttpRestStatus.NOT_FOUND);
		}
		currentShellPc.setPcCode(id);
		currentShellPc.setName(shellPc.getName());
		currentShellPc.setUserName(shellPc.getUserName());
		currentShellPc.setIpAddr(shellPc.getIpAddr());
		currentShellPc.setPassword(shellPc.getPassword());
		currentShellPc.setRemark(shellPc.getRemark());
		shellPcService.updateByPrimaryKeySelective(currentShellPc);//
		currentShellPc.setPassword("");
		//修改日志
	  CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentShellPc,CommonLogImpl.SYSTEM_MANAGE);	

		return new ResponseRestEntity<ShellPc>(currentShellPc, HttpRestStatus.OK);
	}

	//删除指定支付应用
	@PreAuthorize("hasRole('R_MAINTEN_SS_E')")
	@ApiOperation(value = "Delete ShellPc", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<ShellPc> delete(@PathVariable("id") String id) {

		ShellPc shellPc = shellPcService.selectByPrimaryKey(id);
		if (shellPc == null) {
			return new ResponseRestEntity<ShellPc>(HttpRestStatus.NOT_FOUND);
		}

		shellPcService.deleteByPrimaryKey(id);
		//删除日志开始
		ShellPc delBean = new ShellPc();
		delBean.setPcCode(id);              

		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, delBean,CommonLogImpl.SYSTEM_MANAGE);
		//删除日志结束
		return new ResponseRestEntity<ShellPc>(HttpRestStatus.NO_CONTENT);
	}

	
	
}