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
import com.zbensoft.e.payment.api.service.api.PayGetwayTypeService;
import com.zbensoft.e.payment.db.domain.PayGetwayType;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/payGetwayType")
@RestController
public class payGetwayTypeController {
	@Autowired
	PayGetwayTypeService payGetwayTypeService;
	
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	// 查询应用，支持分页
	@PreAuthorize("hasRole('R_PAYMENT_G_T_Q')")
	@ApiOperation(value = "Query application, support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<PayGetwayType>> selectPage(@RequestParam(required = false) String id,
			@RequestParam(required = false) String name,
			@RequestParam(required = false) String remark, @RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		PayGetwayType payGetwayType =new PayGetwayType();
		payGetwayType.setPayGatewayTypeId(id);
		payGetwayType.setName(name);
		payGetwayType.setRemark(remark);
		
		
		List<PayGetwayType> list=payGetwayTypeService.selectPage(payGetwayType);
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = payGetwayTypeService.selectPage(payGetwayType);
			// System.out.println("pageNum:"+pageNum+";pageSize:"+pageSize);
			// System.out.println("list.size:"+list.size());

		} else {
			list = payGetwayTypeService.selectPage(payGetwayType);
		}

		int count = payGetwayTypeService.count(payGetwayType);
		// 分页 end

		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<PayGetwayType>>(new ArrayList<PayGetwayType>(), HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<List<PayGetwayType>>(list, HttpRestStatus.OK, count, count);

	}

	// 查询应用
	@PreAuthorize("hasRole('R_PAYMENT_G_T_Q')")
	@ApiOperation(value = "Query application", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<PayGetwayType> selectByPrimaryKey(@PathVariable("id") String id) {
		PayGetwayType payGetwayType = payGetwayTypeService.selectByPrimaryKey(id);
		if (payGetwayType == null) {
			return new ResponseRestEntity<PayGetwayType>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<PayGetwayType>(payGetwayType, HttpRestStatus.OK);
	}

	// 新增应用
	@PreAuthorize("hasRole('R_PAYMENT_G_T_E')")
	@ApiOperation(value = "Add application", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createPayGetwayType(@Valid @RequestBody PayGetwayType payGetwayType, BindingResult result,
			UriComponentsBuilder ucBuilder) {
		/*consumptionType.setBillConsumptionTypeId(System.currentTimeMillis() + "");*/
		PayGetwayType bean = payGetwayTypeService.selectByPrimaryKey(payGetwayType.getPayGatewayTypeId());
		if (payGetwayTypeService.isPayGetwayTypeExist(payGetwayType)||bean !=null) {
			return new ResponseRestEntity<Void>(HttpRestStatus.CONFLICT, localeMessageSourceService.getMessage("common.create.conflict.message"));
		}

		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}

		payGetwayTypeService.insert(payGetwayType);
		//新增日志
         CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, payGetwayType,CommonLogImpl.FINANCE);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/payGetwayType/{id}").buildAndExpand(payGetwayType.getPayGatewayTypeId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED, localeMessageSourceService.getMessage("common.create.created.message"));

	}

	// 修改应用信息
	@PreAuthorize("hasRole('R_PAYMENT_G_T_E')")
	@ApiOperation(value = "Modify the application information", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<PayGetwayType> updatePayGetwayType(@PathVariable("id") String id, @Valid @RequestBody PayGetwayType payGetwayType,
			BindingResult result) {

		PayGetwayType currentPayGetwayType = payGetwayTypeService.selectByPrimaryKey(id);

		if (currentPayGetwayType == null) {
			return new ResponseRestEntity<PayGetwayType>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}
		currentPayGetwayType.setPayGatewayTypeId(payGetwayType.getPayGatewayTypeId());
		currentPayGetwayType.setName(payGetwayType.getName());
		
		
		
		currentPayGetwayType.setRemark(payGetwayType.getRemark());
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			for (ObjectError error : list) {
				//System.out.println(error.getCode() + "---" + error.getArguments() + "---" + error.getDefaultMessage());
			}

			return new ResponseRestEntity<PayGetwayType>(currentPayGetwayType, HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}

		payGetwayTypeService.updateByPrimaryKey(currentPayGetwayType);
		//修改日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentPayGetwayType,CommonLogImpl.FINANCE);
		return new ResponseRestEntity<PayGetwayType>(currentPayGetwayType, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	// 修改部分应用信息
	@PreAuthorize("hasRole('R_PAYMENT_G_T_E')")
	@ApiOperation(value = "Modify part of the application information", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<PayGetwayType> updatePayGetwayTypeSelective(@PathVariable("id") String id, @RequestBody PayGetwayType payGetwayType) {

		PayGetwayType currentPayGetwayType = payGetwayTypeService.selectByPrimaryKey(id);

		if (currentPayGetwayType == null) {
			return new ResponseRestEntity<PayGetwayType>(HttpRestStatus.NOT_FOUND);
		}
		payGetwayType.setPayGatewayTypeId(id);
		payGetwayTypeService.updateByPrimaryKeySelective(payGetwayType);
		//修改日志
	   CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, payGetwayType,CommonLogImpl.FINANCE);
		return new ResponseRestEntity<PayGetwayType>(currentPayGetwayType, HttpRestStatus.OK);
	}

	// 删除指定应用
	@PreAuthorize("hasRole('R_PAYMENT_G_T_E')")
	@ApiOperation(value = "Delete the specified payGetwayType", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<PayGetwayType> deletePayGetwayType(@PathVariable("id") String id) {

		PayGetwayType payGetwayType = payGetwayTypeService.selectByPrimaryKey(id);
		if (payGetwayType == null) {
			return new ResponseRestEntity<PayGetwayType>(HttpRestStatus.NOT_FOUND);
		}
		payGetwayTypeService.deleteByPrimaryKey(id);
		//删除日志开始
		PayGetwayType delBean = new PayGetwayType();
		delBean.setPayGatewayTypeId(id);
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, delBean,CommonLogImpl.FINANCE);
		//删除日志结束
		return new ResponseRestEntity<PayGetwayType>(HttpRestStatus.NO_CONTENT);
	}

}