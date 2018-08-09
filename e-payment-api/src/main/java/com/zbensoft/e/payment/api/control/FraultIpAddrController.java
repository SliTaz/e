package com.zbensoft.e.payment.api.control;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

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
import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.HttpRestStatusFactory;
import com.zbensoft.e.payment.api.common.LocaleMessageSourceService;
import com.zbensoft.e.payment.api.common.PageHelperUtil;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.service.api.FraultIpAddrService;
import com.zbensoft.e.payment.db.domain.FraultIpAddr;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/fraultIpAddr")
@RestController
public class FraultIpAddrController {
	
	
	private static final Logger log = LoggerFactory.getLogger(FraultIpAddrController.class);

	@Autowired
	FraultIpAddrService fraultIpAddrService;
	
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	@PreAuthorize("hasRole('R_FRAUD_IP_Q')")
	@ApiOperation(value = "Query fraultIpAddr,Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<FraultIpAddr>> selectPage(@RequestParam(required = false) String id,
			@RequestParam(required = false) String ipAddr,
			@RequestParam(required = false) Integer suspiciouType, @RequestParam(required = false) Integer sourceType,
			@RequestParam(required = false) Integer status, @RequestParam(required = false) String remark ,
			@RequestParam(required = false) String start,
			@RequestParam(required = false) String length){
		FraultIpAddr addr = new FraultIpAddr();
		//通过InetAddress处理得到byte[]
		if(ipAddr!=null&&!"".equals(ipAddr)){
			try {
				addr.setIpAddr(InetAddress.getByName(new String(ipAddr)).getAddress());
			} catch (UnknownHostException e) {
				log.error("",e);
			}
		}else{
			addr.setIpAddr(null);
		}

		addr.setSuspiciouType(suspiciouType);
		addr.setStatus(status);
		addr.setSourceType(sourceType);;
		addr.setRemark(remark);

		int count = fraultIpAddrService.count(addr);
		if (count == 0) {
			return new ResponseRestEntity<List<FraultIpAddr>>(new ArrayList<FraultIpAddr>(), HttpRestStatus.NOT_FOUND);
		}

		List<FraultIpAddr> list = null;

		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			// 第一个参数是第几页；第二个参数是每页显示条数。
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = fraultIpAddrService.selectPage(addr);
		} else {
			list = fraultIpAddrService.selectPage(addr);
		}
		
		
		List<FraultIpAddr> listNew = new ArrayList<FraultIpAddr>();
		for(FraultIpAddr bean:list){
			//通过InetAddress将byte[]转成string
			String name=null;
			try {
				name = InetAddress.getByAddress(bean.getIpAddr()).getHostAddress();
				bean.setIpAddrName(name);
			} catch (UnknownHostException e) {
				log.error("",e);
			}
			
			listNew.add(bean);
		}
		


		return new ResponseRestEntity<List<FraultIpAddr>>(listNew, HttpRestStatus.OK, count, count);
	}

	@PreAuthorize("hasRole('R_FRAUD_IP_Q')")
	@ApiOperation(value = "Query Task", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<FraultIpAddr> selectByPrimaryKey(@PathVariable("id") String id) {
		FraultIpAddr bean = new FraultIpAddr();
		bean.setIpAddr(id.getBytes());
		FraultIpAddr addr = fraultIpAddrService.selectByPrimaryKey(bean);
		if (addr == null) {
			return new ResponseRestEntity<FraultIpAddr>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<FraultIpAddr>(addr, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_FRAUD_IP_E')")
	@ApiOperation(value = "Add FraultIpAddr", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createtask(@RequestBody FraultIpAddr addr,BindingResult result,  UriComponentsBuilder ucBuilder) {
		
		addr.setCreateTime(PageHelperUtil.getCurrentDate());
		try {
			addr.setIpAddr(InetAddress.getByName(new String(addr.getIpAddrName())).getAddress());
		} catch (UnknownHostException e) {
			log.error("",e);
		} 
		// 校验
				if (result.hasErrors()) {
					List<ObjectError> list = result.getAllErrors();
					return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
							HttpRestStatusFactory.createStatusMessage(list));
				}
				fraultIpAddrService.insert(addr);
		//新增日志
				
		addr.setIpAddr(null);
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, addr, CommonLogImpl.FRAULT_MANAGEMENT);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/fraultIpAddr/{id}").buildAndExpand(addr.getIpAddr()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,localeMessageSourceService.getMessage("common.create.created.message"));
	}

	@PreAuthorize("hasRole('R_FRAUD_IP_E')")
	@ApiOperation(value = "Edit FraultIpAddr", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<FraultIpAddr> updatetask(@PathVariable("id") String id, @RequestBody FraultIpAddr addr) {
		FraultIpAddr bean = new FraultIpAddr();
		try {
			bean.setIpAddr(InetAddress.getByName(new String(addr.getIpAddrName())).getAddress());
		} catch (UnknownHostException e) {
			log.error("",e);
		}
		FraultIpAddr type = fraultIpAddrService.selectByPrimaryKey(bean);

		if (type == null) {
			return new ResponseRestEntity<FraultIpAddr>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}
		//type.setFraultIpAddrId(addr.getFraultIpAddrId());
		type.setSuspiciouType(addr.getSuspiciouType());
		type.setSourceType(addr.getSourceType());
		type.setStatus(addr.getStatus());
		type.setRemark(addr.getRemark());
		
		fraultIpAddrService.updateByPrimaryKey(type);
		//修改日志
		type.setIpAddr(null);
		type.setIpAddrName(id);
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, type,CommonLogImpl.FRAULT_MANAGEMENT);
		return new ResponseRestEntity<FraultIpAddr>(type, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	@PreAuthorize("hasRole('R_FRAUD_IP_E')")
	@ApiOperation(value = "Edit Part FraultIpAddr", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<FraultIpAddr> updatetaskSelective(@PathVariable("id") String id, @RequestBody FraultIpAddr addr) {
		FraultIpAddr bean = new FraultIpAddr();
		try {
			bean.setIpAddr(InetAddress.getByName(new String(addr.getIpAddr())).getAddress());
		} catch (UnknownHostException e) {
			log.error("",e);
		}
		FraultIpAddr type = fraultIpAddrService.selectByPrimaryKey(bean);

		if (type == null) {
			return new ResponseRestEntity<FraultIpAddr>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}
		//type.setFraultIpAddrId(addr.getFraultIpAddrId());
		type.setIpAddr(addr.getIpAddr());;
		type.setSuspiciouType(addr.getSuspiciouType());
		type.setSourceType(addr.getSourceType());
		type.setStatus(addr.getStatus());
		type.setRemark(addr.getRemark());
		
		fraultIpAddrService.updateByPrimaryKey(type);
		//修改日志
		type.setIpAddr(null);
		type.setIpAddrName(id);
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, type,CommonLogImpl.FRAULT_MANAGEMENT);
		return new ResponseRestEntity<FraultIpAddr>(type, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	@PreAuthorize("hasRole('R_FRAUD_IP_E')")
	@ApiOperation(value = "Delete FraultIpAddr", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<FraultIpAddr> deletetask(@PathVariable("id") String id){
		
		FraultIpAddr bean = new FraultIpAddr();
		try {
			bean.setIpAddr(InetAddress.getByName(new String(id)).getAddress());
		} catch (UnknownHostException e) {
			log.error("",e);
		}
		FraultIpAddr fraultIpAddr = fraultIpAddrService.selectByPrimaryKey(bean);
		
		if (fraultIpAddr == null) {
			return new ResponseRestEntity<FraultIpAddr>(HttpRestStatus.NOT_FOUND);
		}

		fraultIpAddrService.deleteByPrimaryKey(fraultIpAddr);
		//删除日志开始
		FraultIpAddr delBean = new FraultIpAddr();
		delBean.setIpAddrName(id);              
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, delBean,CommonLogImpl.FRAULT_MANAGEMENT);
		//删除日志结束
		return new ResponseRestEntity<FraultIpAddr>(HttpRestStatus.NO_CONTENT);
	}
	// 用户启用
		@ApiOperation(value = "enable the specified FraultIpAddr", notes = "")
		@RequestMapping(value = "/enable/{id}", method = RequestMethod.PUT)
		public ResponseRestEntity<FraultIpAddr> enableTask(@PathVariable("id") String id) {
			FraultIpAddr bean = new FraultIpAddr();
			try {
				bean.setIpAddr(InetAddress.getByName(new String(id)).getAddress());
			} catch (UnknownHostException e) {
				log.error("",e);
			}
			FraultIpAddr task = fraultIpAddrService.selectByPrimaryKey(bean);
			if (task == null) {
				return new ResponseRestEntity<FraultIpAddr>(HttpRestStatus.NOT_FOUND);
			}
			//改变用户状态 0:启用  1:停用
			task.setStatus(0);
			fraultIpAddrService.updateByPrimaryKey(task);
			//修改日志
			task.setIpAddr(null);
			task.setIpAddrName(id);
	        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_ACTIVATE, task,CommonLogImpl.FRAULT_MANAGEMENT);
			return new ResponseRestEntity<FraultIpAddr>(HttpRestStatus.OK);
		}
		
		// 用户停用
		@PreAuthorize("hasRole('R_FRAUD_IP_E')")
		@ApiOperation(value = "enable the specified FraultIpAddr", notes = "")
		@RequestMapping(value = "/disable/{id}", method = RequestMethod.PUT)
		public ResponseRestEntity<FraultIpAddr> disableSysUser(@PathVariable("id") String id) {

			FraultIpAddr bean = new FraultIpAddr();
			try {
				bean.setIpAddr(InetAddress.getByName(new String(id)).getAddress());
			} catch (UnknownHostException e) {
				log.error("",e);
			}
			FraultIpAddr task = fraultIpAddrService.selectByPrimaryKey(bean);
			if (task == null) {
				return new ResponseRestEntity<FraultIpAddr>(HttpRestStatus.NOT_FOUND);
			}
			//改变用户状态 0:启用  1:停用
			task.setStatus(1);
			fraultIpAddrService.updateByPrimaryKey(task);
			//修改日志
			task.setIpAddr(null);
			task.setIpAddrName(id);
	        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INACTIVATE, task,CommonLogImpl.FRAULT_MANAGEMENT);
			return new ResponseRestEntity<FraultIpAddr>(HttpRestStatus.OK);
		}
}
