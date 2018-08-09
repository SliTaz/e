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
import com.zbensoft.e.payment.api.service.api.BankCardTypeService;
import com.zbensoft.e.payment.api.service.api.BankInfoService;
import com.zbensoft.e.payment.api.service.api.PayGatewayService;
import com.zbensoft.e.payment.api.service.api.PayGetwayTypeService;
import com.zbensoft.e.payment.db.domain.BankInfo;
import com.zbensoft.e.payment.db.domain.PayGateway;
import com.zbensoft.e.payment.db.domain.PayGetwayType;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/payGateway")
@RestController
public class PayGatewayController {
	@Autowired
	PayGatewayService payGatewayService;
	
	@Autowired
	BankInfoService bankInfoService;
	
	@Autowired
	PayGetwayTypeService payGetwayTypeService;
	
	@Autowired
	BankCardTypeService bankCardTypeService;
	
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	@PreAuthorize("hasRole('R_PAYMENT_G_Q') or hasRole('R_TRADE_I_Q')")
	@ApiOperation(value = "Query PayGateway，Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<PayGateway>> selectPage(@RequestParam(required = false) String id,
			@RequestParam(required = false) String bankId,@RequestParam(required = false) String payGatewayTypeId,
		    @RequestParam(required = false) Integer status,
			@RequestParam(required = false) String start,@RequestParam(required = false) String length) {
		PayGateway payGateway = new PayGateway();
		payGateway.setPayGatewayId(id);
		//payGateway.setPayGetwayType(payGetwayType);
		payGateway.setPayGatewayTypeId(payGatewayTypeId);
		//payGateway.setBankType(bankType);
		payGateway.setStatus(status);
		payGateway.setBankId(bankId);
		int count = payGatewayService.count(payGateway);
		if (count == 0) {
			return new ResponseRestEntity<List<PayGateway>>(new ArrayList<PayGateway>(), HttpRestStatus.NOT_FOUND);
		}
		List<PayGateway> list = null;
		// 分页 start
				if (start != null && length != null) {// 需要进行分页
					/*
					 * 第一个参数是第几页；第二个参数是每页显示条数。
					 */
					int pageNum = PageHelperUtil.getPageNum(start, length);
					int pageSize = PageHelperUtil.getPageSize(start, length);
					PageHelper.startPage(pageNum, pageSize);
					list = payGatewayService.selectPage(payGateway);
					// System.out.println("pageNum:"+pageNum+";pageSize:"+pageSize);
					// System.out.println("list.size:"+list.size());

				} else {
					list = payGatewayService.selectPage(payGateway);
				}

				// 分页 end
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<PayGateway>>(new ArrayList<PayGateway>(),HttpRestStatus.NOT_FOUND);
		}
		List<PayGateway> listNew = new ArrayList<PayGateway>();
		for(PayGateway bean:list){
			bean.setRecvGatewayId(bean.getPayGatewayId());
			BankInfo bankInfo = bankInfoService.selectByPrimaryKey(bean.getBankId());
			PayGetwayType payGetwayType1=payGetwayTypeService.selectByPrimaryKey(bean.getPayGatewayTypeId());
		//	BankCardType bankCardType=bankCardTypeService.selectByPrimaryKey(bean.getBankType());
			if(bankInfo!=null){
				bean.setBankName(bankInfo.getName());
			}
			if(payGetwayType1!=null){
				bean.setPayGatewayTypeName(payGetwayType1.getName());
			}
			/*if(bankCardType!=null){
				bean.setBankCardTypeName(bankCardType.getName());
			}*/
			listNew.add(bean);
		}
		
		return new ResponseRestEntity<List<PayGateway>>(listNew, HttpRestStatus.OK,count,count);
	}

	@PreAuthorize("hasRole('R_PAYMENT_G_Q')")
	@ApiOperation(value = "Query PayGateway", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<PayGateway> selectByPrimaryKey(@PathVariable("id") String id) {
		PayGateway payGateway = payGatewayService.selectByPrimaryKey(id);
		if (payGateway == null) {
			return new ResponseRestEntity<PayGateway>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<PayGateway>(payGateway, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_PAYMENT_G_E')")
	@ApiOperation(value = "Add PayGateway", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createPayGateway(@Valid @RequestBody PayGateway payGateway, BindingResult result, UriComponentsBuilder ucBuilder) {
		//payGateway.setPayGatewayId(System.currentTimeMillis()+"");
		
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}

		payGatewayService.insert(payGateway);
		//新增日志
       CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, payGateway,CommonLogImpl.PAYMENT);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/payGateway/{id}").buildAndExpand(payGateway.getPayGatewayId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,localeMessageSourceService.getMessage("common.create.created.message"));
	}

	@PreAuthorize("hasRole('R_PAYMENT_G_E')")
	@ApiOperation(value = "Edit PayGateway", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<PayGateway> updatePayGateway(@PathVariable("id") String id,@Valid @RequestBody PayGateway payGateway
			, BindingResult result) {

		PayGateway currentPayGateway = payGatewayService.selectByPrimaryKey(id);

		if (currentPayGateway == null) {
			return new ResponseRestEntity<PayGateway>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}
		currentPayGateway.setName(payGateway.getName());
		currentPayGateway.setBankId(payGateway.getBankId());
		currentPayGateway.setPayGatewayTypeId(payGateway.getPayGatewayTypeId());
	//	currentPayGateway.setBankType(payGateway.getBankType());
		currentPayGateway.setStatus(payGateway.getStatus());
		currentPayGateway.setRemark(payGateway.getRemark());
		
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			for (ObjectError error : list) {
				//System.out.println(error.getCode() + "---" + error.getArguments() + "---" + error.getDefaultMessage());
			}

			return new ResponseRestEntity<PayGateway>(currentPayGateway,HttpRestStatusFactory.createStatus(list),HttpRestStatusFactory.createStatusMessage(list));
		}
		payGatewayService.updateByPrimaryKey(currentPayGateway);
		//修改日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentPayGateway,CommonLogImpl.PAYMENT);
		return new ResponseRestEntity<PayGateway>(currentPayGateway, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	@PreAuthorize("hasRole('R_PAYMENT_G_E')")
	@ApiOperation(value = "Edit Part PayGateway", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<PayGateway> updatePayGatewaySelective(@PathVariable("id") String id, @RequestBody PayGateway payGateway) {

		PayGateway currentPayGateway = payGatewayService.selectByPrimaryKey(id);

		if (currentPayGateway == null) {
			return new ResponseRestEntity<PayGateway>(HttpRestStatus.NOT_FOUND);
		}
		currentPayGateway.setPayGatewayId(id);
		currentPayGateway.setName(payGateway.getName());
		currentPayGateway.setBankId(payGateway.getBankId());
		currentPayGateway.setPayGatewayTypeId(payGateway.getPayGatewayTypeId());
	//	currentPayGateway.setBankType(payGateway.getBankType());
		currentPayGateway.setStatus(payGateway.getStatus());
		currentPayGateway.setRemark(payGateway.getRemark());
		payGatewayService.updateByPrimaryKeySelective(payGateway);
		//修改日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, payGateway,CommonLogImpl.PAYMENT);
		return new ResponseRestEntity<PayGateway>(currentPayGateway, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_PAYMENT_G_E')")
	@ApiOperation(value = "Delete PayGateway", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<PayGateway> deletePayGateway(@PathVariable("id") String id) {

		PayGateway payGateway = payGatewayService.selectByPrimaryKey(id);
		if (payGateway == null) {
			return new ResponseRestEntity<PayGateway>(HttpRestStatus.NOT_FOUND);
		}

		payGatewayService.deleteByPrimaryKey(id);
		
		//删除日志开始
		PayGateway delBean = new PayGateway();
		delBean.setPayGatewayId(id);

		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, delBean,CommonLogImpl.PAYMENT);
		//删除日志结束
		return new ResponseRestEntity<PayGateway>(HttpRestStatus.NO_CONTENT);
	}
	// 用户启用
	@PreAuthorize("hasRole('R_PAYMENT_G_E')")
	@ApiOperation(value = "enable the specified payCalcPrice", notes = "")
	@RequestMapping(value = "/enable/{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<PayGateway> enablePayCalcPrice(@PathVariable("id") String id) {

		PayGateway payGateway = payGatewayService.selectByPrimaryKey(id);
		if (payGateway == null) {
			return new ResponseRestEntity<PayGateway>(HttpRestStatus.NOT_FOUND);
		}
		
		payGateway.setStatus(1);
		payGatewayService.updateByPrimaryKey(payGateway);
		//修改日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_ACTIVATE, payGateway,CommonLogImpl.PAYMENT);	
		return new ResponseRestEntity<PayGateway>(HttpRestStatus.OK);
	}
	
	// 用户停用
	@PreAuthorize("hasRole('R_PAYMENT_G_E')")
	@ApiOperation(value = "enable the specified payCalcPrice", notes = "")
	@RequestMapping(value = "/disable/{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<PayGateway> disablePayCalcPrice(@PathVariable("id") String id) {

		PayGateway payGateway = payGatewayService.selectByPrimaryKey(id);
		if (payGateway == null) {
			return new ResponseRestEntity<PayGateway>(HttpRestStatus.NOT_FOUND);
		}
		
		payGateway.setStatus(0);
		payGatewayService.updateByPrimaryKey(payGateway);
		//修改日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INACTIVATE, payGateway,CommonLogImpl.PAYMENT);	
		return new ResponseRestEntity<PayGateway>(HttpRestStatus.OK);
	}
}