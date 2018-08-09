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
import com.zbensoft.e.payment.api.common.IDGenerate;
import com.zbensoft.e.payment.api.common.LocaleMessageSourceService;
import com.zbensoft.e.payment.api.common.PageHelperUtil;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.service.api.PayCalcPriceService;
import com.zbensoft.e.payment.api.service.api.PayGatewayService;
import com.zbensoft.e.payment.db.domain.AlarmLevel;
import com.zbensoft.e.payment.db.domain.PayApp;
import com.zbensoft.e.payment.db.domain.PayCalcPrice;
import com.zbensoft.e.payment.db.domain.PayGateway;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/payCalcPrice")
@RestController
public class PayCalcPriceController {
	@Autowired
	PayCalcPriceService payCalcPriceService;
	
	@Autowired
	PayGatewayService payGatewayService;
	
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	@PreAuthorize("hasRole('R_PAYMENT_C_P_Q')")
	@ApiOperation(value = "Query PayCalcPrice，Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<PayCalcPrice>> selectPage(@RequestParam(required = false) String id,
			@RequestParam(required = false) String payGatewayId,
			@RequestParam(required = false) String name,
			@RequestParam(required = false) String handleClass,
			@RequestParam(required = false) String userName,
			@RequestParam(required = false) Integer status,
			@RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		PayCalcPrice payCalcPrice = new PayCalcPrice();
		payCalcPrice.setPayCalcPriceId(id);
		payCalcPrice.setPayGatewayId(payGatewayId);
		payCalcPrice.setName(name);
		payCalcPrice.setHandleClass(handleClass);
		payCalcPrice.setUserName(userName);
		payCalcPrice.setStatus(status);
		int count = payCalcPriceService.count(payCalcPrice);
		if (count == 0) {
			return new ResponseRestEntity<List<PayCalcPrice>>(new ArrayList<PayCalcPrice>(), HttpRestStatus.NOT_FOUND);
		}
		List<PayCalcPrice> list = null;
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = payCalcPriceService.selectPage(payCalcPrice);
			// System.out.println("pageNum:"+pageNum+";pageSize:"+pageSize);
			// System.out.println("list.size:"+list.size());

		} else {
			list = payCalcPriceService.selectPage(payCalcPrice);
		}
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<PayCalcPrice>>(new ArrayList<PayCalcPrice>(),HttpRestStatus.NOT_FOUND);
		}
		//查询列表由编号展示名称
		List<PayCalcPrice> listNew = new ArrayList<PayCalcPrice>();
		for(PayCalcPrice bean:list){
			PayGateway payGateway = payGatewayService.selectByPrimaryKey(bean.getPayGatewayId());
			if(payGateway!=null){
				bean.setPayGatewayName(payGateway.getName());
			}
			listNew.add(bean);
		}
		return new ResponseRestEntity<List<PayCalcPrice>>(listNew, HttpRestStatus.OK, count, count);
	}

	@PreAuthorize("hasRole('R_PAYMENT_C_P_Q')")
	@ApiOperation(value = "Query PayCalcPrice", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<PayCalcPrice> selectByPrimaryKey(@PathVariable("id") String id) {
		PayCalcPrice payCalcPrice = payCalcPriceService.selectByPrimaryKey(id);
		if (payCalcPrice == null) {
			return new ResponseRestEntity<PayCalcPrice>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<PayCalcPrice>(payCalcPrice, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_PAYMENT_C_P_E')")
	@ApiOperation(value = "Add PayCalcPrice", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createPayCalcPrice(@Valid @RequestBody PayCalcPrice payCalcPrice,BindingResult result,
			UriComponentsBuilder ucBuilder) {

		payCalcPrice.setPayCalcPriceId(IDGenerate.generateCommOne(IDGenerate.PAY_CALC_PRICE));
		if (payCalcPriceService.isPayCalcPriceExist(payCalcPrice)) {
			return new ResponseRestEntity<Void>(HttpRestStatus.CONFLICT,localeMessageSourceService.getMessage("common.create.conflict.message"));
		}
		
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}

		payCalcPriceService.insert(payCalcPrice);
		//新增日志
      CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, payCalcPrice,CommonLogImpl.PAYMENT);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(
				ucBuilder.path("/payCalcPrice/{id}").buildAndExpand(payCalcPrice.getPayCalcPriceId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,localeMessageSourceService.getMessage("common.create.created.message"));
	}

	@PreAuthorize("hasRole('R_PAYMENT_C_P_E')")
	@ApiOperation(value = "Edit PayCalcPrice", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<PayCalcPrice> updatePayCalcPrice(@PathVariable("id") String id,
			@Valid @RequestBody PayCalcPrice payCalcPrice, BindingResult result) {

		PayCalcPrice currentPayCalcPrice = payCalcPriceService.selectByPrimaryKey(id);

		if (currentPayCalcPrice == null) {
			return new ResponseRestEntity<PayCalcPrice>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}

		currentPayCalcPrice.setPayGatewayId(payCalcPrice.getPayGatewayId());;
		currentPayCalcPrice.setName(payCalcPrice.getName());
		currentPayCalcPrice.setHandleClass(payCalcPrice.getHandleClass());
		currentPayCalcPrice.setUserName(payCalcPrice.getUserName());
		currentPayCalcPrice.setStatus(payCalcPrice.getStatus());
		currentPayCalcPrice.setRemark(payCalcPrice.getRemark());
		
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			for (ObjectError error : list) {
				//System.out.println(error.getCode() + "---" + error.getArguments() + "---" + error.getDefaultMessage());
			}

			return new ResponseRestEntity<PayCalcPrice>(currentPayCalcPrice,HttpRestStatusFactory.createStatus(list),HttpRestStatusFactory.createStatusMessage(list));
		}
		payCalcPriceService.updateByPrimaryKey(currentPayCalcPrice);
		//修改日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentPayCalcPrice,CommonLogImpl.PAYMENT);
		return new ResponseRestEntity<PayCalcPrice>(currentPayCalcPrice, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	@PreAuthorize("hasRole('R_PAYMENT_C_P_E')")
	@ApiOperation(value = "Edit Part PayCalcPrice", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<PayCalcPrice> updatePayCalcPriceSelective(@PathVariable("id") String id,
			@RequestBody PayCalcPrice payCalcPrice) {

		PayCalcPrice currentPayCalcPrice = payCalcPriceService.selectByPrimaryKey(id);

		if (currentPayCalcPrice == null) {
			return new ResponseRestEntity<PayCalcPrice>(HttpRestStatus.NOT_FOUND);
		}
		currentPayCalcPrice.setPayCalcPriceId(id);
		currentPayCalcPrice.setPayGatewayId(payCalcPrice.getPayGatewayId());;
		currentPayCalcPrice.setName(payCalcPrice.getName());
		currentPayCalcPrice.setHandleClass(payCalcPrice.getHandleClass());
		currentPayCalcPrice.setUserName(payCalcPrice.getUserName());
		currentPayCalcPrice.setStatus(payCalcPrice.getStatus());
		currentPayCalcPrice.setRemark(payCalcPrice.getRemark());
		payCalcPriceService.updateByPrimaryKeySelective(currentPayCalcPrice);
		//修改日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentPayCalcPrice,CommonLogImpl.PAYMENT);
		return new ResponseRestEntity<PayCalcPrice>(currentPayCalcPrice, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_PAYMENT_C_P_E')")
	@ApiOperation(value = "Delete PayCalcPrice", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<PayCalcPrice> deletePayCalcPrice(@PathVariable("id") String id) {

		PayCalcPrice payCalcPrice = payCalcPriceService.selectByPrimaryKey(id);
		if (payCalcPrice == null) {
			return new ResponseRestEntity<PayCalcPrice>(HttpRestStatus.NOT_FOUND);
		}

		payCalcPriceService.deleteByPrimaryKey(id);
		//删除日志开始
		PayCalcPrice delBean = new PayCalcPrice();
		delBean.setPayCalcPriceId(id);              
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, delBean,CommonLogImpl.PAYMENT);
		//删除日志结束
		return new ResponseRestEntity<PayCalcPrice>(HttpRestStatus.NO_CONTENT);
	}
	// 用户启用
	@PreAuthorize("hasRole('R_PAYMENT_C_P_E')")
	@ApiOperation(value = "enable the specified payCalcPrice", notes = "")
	@RequestMapping(value = "/enable/{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<PayCalcPrice> enablePayCalcPrice(@PathVariable("id") String id) {

		PayCalcPrice payCalcPrice = payCalcPriceService.selectByPrimaryKey(id);
		if (payCalcPrice == null) {
			return new ResponseRestEntity<PayCalcPrice>(HttpRestStatus.NOT_FOUND);
		}
		
		payCalcPrice.setStatus(1);
		payCalcPriceService.updateByPrimaryKey(payCalcPrice);
		//修改日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_ACTIVATE, payCalcPrice,CommonLogImpl.PAYMENT);	
		return new ResponseRestEntity<PayCalcPrice>(HttpRestStatus.OK);
	}
	
	// 用户停用
	@PreAuthorize("hasRole('R_PAYMENT_C_P_E')")
	@ApiOperation(value = "enable the specified payCalcPrice", notes = "")
	@RequestMapping(value = "/disable/{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<PayCalcPrice> disablePayCalcPrice(@PathVariable("id") String id) {

		PayCalcPrice payCalcPrice = payCalcPriceService.selectByPrimaryKey(id);
		if (payCalcPrice == null) {
			return new ResponseRestEntity<PayCalcPrice>(HttpRestStatus.NOT_FOUND);
		}
		
		payCalcPrice.setStatus(0);
		payCalcPriceService.updateByPrimaryKey(payCalcPrice);
		//修改日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INACTIVATE, payCalcPrice,CommonLogImpl.PAYMENT);	
		return new ResponseRestEntity<PayCalcPrice>(HttpRestStatus.OK);
	}
}