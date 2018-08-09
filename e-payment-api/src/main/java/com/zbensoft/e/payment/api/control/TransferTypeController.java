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
import com.zbensoft.e.payment.api.service.api.TransferTypeService;
import com.zbensoft.e.payment.db.domain.TransferType;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/transferType")
@RestController
public class TransferTypeController {
	@Autowired
	TransferTypeService transferTypeService;
	
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	// 查询应用，支持分页
	@PreAuthorize("hasRole('R_TRADE_T_Q') or hasRole('CONSUMER')")
	@ApiOperation(value = "Query TransferType, support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<TransferType>> selectPage(@RequestParam(required = false) String id,
			@RequestParam(required = false) String name,
			@RequestParam(required = false) Integer deleteFlag,
			@RequestParam(required = false) String remark, @RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		TransferType transferType = new TransferType();
		transferType.setTransferTypeId(id);
		transferType.setName(name);
		transferType.setDeleteFlag(0);
		
		transferType.setRemark(remark);
		List<TransferType> list = transferTypeService.selectPage(transferType);
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = transferTypeService.selectPage(transferType);
			// System.out.println("pageNum:"+pageNum+";pageSize:"+pageSize);
			// System.out.println("list.size:"+list.size());

		} else {
			list = transferTypeService.selectPage(transferType);
		}

		int count = transferTypeService.count(transferType);
		// 分页 end

		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<TransferType>>(new ArrayList<TransferType>(), HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<List<TransferType>>(list, HttpRestStatus.OK, count, count);

	}

	// 查询应用
	@PreAuthorize("hasRole('R_TRADE_T_Q')")
	@ApiOperation(value = "Query TransferType", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<TransferType> selectByPrimaryKey(@PathVariable("id") String id) {
		TransferType transferType = transferTypeService.selectByPrimaryKey(id);
		if (transferType == null) {
			return new ResponseRestEntity<TransferType>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<TransferType>(transferType, HttpRestStatus.OK);
	}

	// 新增应用
	@PreAuthorize("hasRole('R_TRADE_T_E')")
	@ApiOperation(value = "Add TransferType", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createTransferType(@Valid @RequestBody TransferType transferType, BindingResult result,
			UriComponentsBuilder ucBuilder) {
	/*	transferType.setTransferTypeId(System.currentTimeMillis() + "");*/
		TransferType bean = transferTypeService.selectByPrimaryKey(transferType.getTransferTypeId());
		transferType.setDeleteFlag(0);
		if (transferTypeService.isTransferTypeExist(transferType)||bean !=null) {
			return new ResponseRestEntity<Void>(HttpRestStatus.CONFLICT, localeMessageSourceService.getMessage("common.create.conflict.message"));
		}

		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}

		transferTypeService.insert(transferType);
		//新增日志
       CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, transferType,CommonLogImpl.FINANCE);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/transferType/{id}").buildAndExpand(transferType.getTransferTypeId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED, localeMessageSourceService.getMessage("common.create.created.message"));

	}

	// 修改应用信息
	@PreAuthorize("hasRole('R_TRADE_T_E')")
	@ApiOperation(value = "Edit TransferType", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<TransferType> updateTransferType(@PathVariable("id") String id, @Valid @RequestBody TransferType transferType,
			BindingResult result) {

		TransferType currentTransferType = transferTypeService.selectByPrimaryKey(id);

		if (currentTransferType == null) {
			return new ResponseRestEntity<TransferType>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}
		currentTransferType.setTransferTypeId(transferType.getTransferTypeId());
		currentTransferType.setName(transferType.getName());
		currentTransferType.setDeleteFlag(transferType.getDeleteFlag());
		
		
		
		currentTransferType.setRemark(transferType.getRemark());
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			for (ObjectError error : list) {
				//System.out.println(error.getCode() + "---" + error.getArguments() + "---" + error.getDefaultMessage());
			}

			return new ResponseRestEntity<TransferType>(currentTransferType, HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}

		transferTypeService.updateByPrimaryKey(currentTransferType);
		//修改日志
       CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentTransferType,CommonLogImpl.FINANCE);
		return new ResponseRestEntity<TransferType>(currentTransferType, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	// 修改部分应用信息
	@PreAuthorize("hasRole('R_TRADE_T_E')")
	@ApiOperation(value = "Edit part TransferType", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<TransferType> updateTransferTypeSelective(@PathVariable("id") String id, @RequestBody TransferType transferType) {

		TransferType currentTransferType = transferTypeService.selectByPrimaryKey(id);

		if (currentTransferType == null) {
			return new ResponseRestEntity<TransferType>(HttpRestStatus.NOT_FOUND);
		}
		transferType.setTransferTypeId(id);
		transferTypeService.updateByPrimaryKeySelective(transferType);
		//修改日志
	    CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, transferType,CommonLogImpl.FINANCE);
		return new ResponseRestEntity<TransferType>(currentTransferType, HttpRestStatus.OK);
	}

	// 删除指定应用
	@PreAuthorize("hasRole('R_TRADE_T_E')")
	@ApiOperation(value = "Delete the specified transferType", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<TransferType> deleteTransferType(@PathVariable("id") String id) {

		TransferType transferType = transferTypeService.selectByPrimaryKey(id);
		if (transferType == null) {
			return new ResponseRestEntity<TransferType>(HttpRestStatus.NOT_FOUND);
		}
		transferType.setDeleteFlag(1);
		transferTypeService.updateByPrimaryKeySelective(transferType);
		//删除日志开始
		TransferType delBean = new TransferType();
		delBean.setTransferTypeId(id);
		delBean.setDeleteFlag(1);
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, delBean,CommonLogImpl.FINANCE);
		//删除日志结束
		return new ResponseRestEntity<TransferType>(HttpRestStatus.NO_CONTENT);
	}
}