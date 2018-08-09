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
import com.zbensoft.e.payment.api.service.api.PayRuleService;
import com.zbensoft.e.payment.db.domain.PayGateway;
import com.zbensoft.e.payment.db.domain.PayRule;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/payRule")
@RestController
public class PayRuleController {
	@Autowired
	PayRuleService payRuleService;
	
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	@ApiOperation(value = "Query PayRule，Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<PayRule>> selectPage(@RequestParam(required = false) String id,
			@RequestParam(required = false) String userId,@RequestParam(required = false) Integer status,
			@RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		PayRule payRule = new PayRule();
		payRule.setPayRuleId(id);
		payRule.setUserId(userId);
		payRule.setStatus(status);
		int count = payRuleService.count(payRule);
		if (count == 0) {
			return new ResponseRestEntity<List<PayRule>>(new ArrayList<PayRule>(), HttpRestStatus.NOT_FOUND);
		}
		List<PayRule> list = null;
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = payRuleService.selectPage(payRule);
			// System.out.println("pageNum:"+pageNum+";pageSize:"+pageSize);
			// System.out.println("list.size:"+list.size());

		} else {
			list = payRuleService.selectPage(payRule);
		}
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<PayRule>>(new ArrayList<PayRule>(),HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<List<PayRule>>(list, HttpRestStatus.OK, count, count);
	}

	@ApiOperation(value = "Query PayRule", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<PayRule> selectByPrimaryKey(@PathVariable("id") String id) {
		PayRule payRule = payRuleService.selectByPrimaryKey(id);
		if (payRule == null) {
			return new ResponseRestEntity<PayRule>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<PayRule>(payRule, HttpRestStatus.OK);
	}

	@ApiOperation(value = "Add PayRule", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createPayRule(@Valid @RequestBody PayRule payRule,BindingResult result, UriComponentsBuilder ucBuilder) {
		payRule.setPayRuleId(IDGenerate.generateCommOne(IDGenerate.PAY_RULE));
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}
		payRuleService.insert(payRule);
		//新增日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, payRule,CommonLogImpl.PAYMENT);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/payRule/{id}").buildAndExpand(payRule.getPayRuleId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,localeMessageSourceService.getMessage("common.create.created.message"));
	}

	@ApiOperation(value = "Edit PayRule", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<PayRule> updatePayRule(@PathVariable("id") String id,@Valid @RequestBody PayRule payRule, BindingResult result) {

		PayRule currentPayRule = payRuleService.selectByPrimaryKey(id);

		if (currentPayRule == null) {
			return new ResponseRestEntity<PayRule>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}

		currentPayRule.setUserId(payRule.getUserId());
		currentPayRule.setLimitMoney(payRule.getLimitMoney());
		currentPayRule.setLimitNumber(payRule.getLimitNumber());
		currentPayRule.setStatus(payRule.getStatus());
		currentPayRule.setRemark(payRule.getRemark());
		
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			for (ObjectError error : list) {
				//System.out.println(error.getCode() + "---" + error.getArguments() + "---" + error.getDefaultMessage());
			}

			return new ResponseRestEntity<PayRule>(currentPayRule,HttpRestStatusFactory.createStatus(list),HttpRestStatusFactory.createStatusMessage(list));
		}
		payRuleService.updateByPrimaryKey(currentPayRule);
		//修改日志
       CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentPayRule,CommonLogImpl.PAYMENT);
		return new ResponseRestEntity<PayRule>(currentPayRule, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	@ApiOperation(value = "Edit Part PayRule", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<PayRule> updatePayRuleSelective(@PathVariable("id") String id,
			@RequestBody PayRule payRule) {

		PayRule currentPayRule = payRuleService.selectByPrimaryKey(id);

		if (currentPayRule == null) {
			return new ResponseRestEntity<PayRule>(HttpRestStatus.NOT_FOUND);
		}
		payRule.setPayRuleId(id);
		payRuleService.updateByPrimaryKeySelective(payRule);
		//修改日志
	   CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, payRule,CommonLogImpl.PAYMENT);
		return new ResponseRestEntity<PayRule>(currentPayRule, HttpRestStatus.OK);
	}

	@ApiOperation(value = "Delete PayRule", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<PayRule> deletePayRule(@PathVariable("id") String id) {

		PayRule payRule = payRuleService.selectByPrimaryKey(id);
		if (payRule == null) {
			return new ResponseRestEntity<PayRule>(HttpRestStatus.NOT_FOUND);
		}

		payRuleService.deleteByPrimaryKey(id);
		//删除日志开始
		PayRule delBean = new PayRule();
		delBean.setPayRuleId(id);

		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, delBean,CommonLogImpl.PAYMENT);
		//删除日志结束
		return new ResponseRestEntity<PayRule>(HttpRestStatus.NO_CONTENT);
	}
	// 用户启用
	@ApiOperation(value = "enable the specified payCalcPrice", notes = "")
	@RequestMapping(value = "/enable/{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<PayRule> enablePayCalcPrice(@PathVariable("id") String id) {

		PayRule payRule = payRuleService.selectByPrimaryKey(id);
		if (payRule == null) {
			return new ResponseRestEntity<PayRule>(HttpRestStatus.NOT_FOUND);
		}
		
		payRule.setStatus(1);
		payRuleService.updateByPrimaryKey(payRule);
		//修改日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_ACTIVATE, payRule,CommonLogImpl.PAYMENT);	
		return new ResponseRestEntity<PayRule>(HttpRestStatus.OK);
	}
	
	// 用户停用
	@ApiOperation(value = "enable the specified payCalcPrice", notes = "")
	@RequestMapping(value = "/disable/{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<PayRule> disablePayCalcPrice(@PathVariable("id") String id) {

		PayRule payRule = payRuleService.selectByPrimaryKey(id);
		if (payRule == null) {
			return new ResponseRestEntity<PayRule>(HttpRestStatus.NOT_FOUND);
		}
		
		payRule.setStatus(0);
		payRuleService.updateByPrimaryKey(payRule);
		//修改日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INACTIVATE, payRule,CommonLogImpl.PAYMENT);	
		return new ResponseRestEntity<PayRule>(HttpRestStatus.OK);
	}
}

