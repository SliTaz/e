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

import com.zbensoft.e.payment.api.common.CommonLogImpl;
import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.HttpRestStatusFactory;
import com.zbensoft.e.payment.api.common.LocaleMessageSourceService;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.service.api.FraultModelRuleService;
import com.zbensoft.e.payment.db.domain.FraultModelRule;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/fraultModelRule")
@RestController
public class FraultModelRuleController {
	@Autowired
	FraultModelRuleService fraultModelRuleService;

	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	@ApiOperation(value = "Query FraultModelRule，Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<FraultModelRule>> selectPage(@RequestParam(required = false) String id,
			@RequestParam(required = false) String start, @RequestParam(required = false) String length) {
		FraultModelRule fraultModelRule = new FraultModelRule();
		List<FraultModelRule> list = fraultModelRuleService.selectPage(fraultModelRule);
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<FraultModelRule>>(new ArrayList<FraultModelRule>(),
					HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<List<FraultModelRule>>(list, HttpRestStatus.OK);
	}

	@ApiOperation(value = "Query FraultModelRule", notes = "")
	@RequestMapping(value = "/{fraultModelId}/{fraultRuleId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<FraultModelRule> selectByPrimaryKey(@PathVariable("fraultModelId") String fraultModelId,
			@PathVariable("fraultRuleId") String fraultRuleId) {
		FraultModelRule bean = new FraultModelRule();
		bean.setFraultModelId(fraultModelId);
		bean.setFraultRuleId(fraultRuleId);
		FraultModelRule fraultModelRule = fraultModelRuleService.selectByPrimaryKey(bean);
		if (fraultModelRule == null) {
			return new ResponseRestEntity<FraultModelRule>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<FraultModelRule>(fraultModelRule, HttpRestStatus.OK);
	}
	
	
	@ApiOperation(value = "Query FraultModelRule", notes = "")
	@RequestMapping(value = "/{fraultRuleId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<List<FraultModelRule>> selectByPayAppId(@PathVariable("fraultRuleId") String fraultRuleId) {
		List<FraultModelRule> list = fraultModelRuleService.selectByRuleId(fraultRuleId);
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<FraultModelRule>>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<List<FraultModelRule>>(list, HttpRestStatus.OK);
	}

	@ApiOperation(value = "Add FraultModelRule", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createFraultModelRule(@Valid @RequestBody FraultModelRule fraultModelRule,
			BindingResult result, UriComponentsBuilder ucBuilder) {
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}
		fraultModelRuleService.insert(fraultModelRule);
		//新增日志
CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, fraultModelRule,CommonLogImpl.FRAULT_MANAGEMENT);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(
				ucBuilder.path("/fraultModelRule/{id}").buildAndExpand(fraultModelRule.getFraultModelId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED, localeMessageSourceService.getMessage("common.create.created.message"));
	}

	@ApiOperation(value = "Edit FraultModelRule", notes = "")
	@RequestMapping(value = "/{fraultModelId}/{fraultRuleId}", method = RequestMethod.PUT)
	public ResponseRestEntity<FraultModelRule> updateFraultModelRule(
			@PathVariable("fraultModelId") String fraultModelId, @PathVariable("fraultRuleId") String fraultRuleId,
			@Valid @RequestBody FraultModelRule fraultModelRule, BindingResult result) {
		FraultModelRule bean = new FraultModelRule();
		bean.setFraultModelId(fraultModelId);
		bean.setFraultRuleId(fraultRuleId);
		FraultModelRule currentFraultModelRule = fraultModelRuleService.selectByPrimaryKey(bean);

		if (currentFraultModelRule == null) {
			return new ResponseRestEntity<FraultModelRule>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}

		currentFraultModelRule.setFraultModelId(fraultModelRule.getFraultModelId());
		currentFraultModelRule.setFraultRuleId(fraultModelRule.getFraultModelId());
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			for (ObjectError error : list) {
				//System.out.println(error.getCode() + "---" + error.getArguments() + "---" + error.getDefaultMessage());
			}

			return new ResponseRestEntity<FraultModelRule>(currentFraultModelRule,
					HttpRestStatusFactory.createStatus(list), HttpRestStatusFactory.createStatusMessage(list));
		}
		fraultModelRuleService.updateByPrimaryKey(currentFraultModelRule);
		//修改日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentFraultModelRule,CommonLogImpl.FRAULT_MANAGEMENT);
		return new ResponseRestEntity<FraultModelRule>(currentFraultModelRule, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	@ApiOperation(value = "Edit Part AlarmType", notes = "")
	@RequestMapping(value = "/{fraultModelId}/{fraultRuleId}", method = RequestMethod.PATCH)
	public ResponseRestEntity<FraultModelRule> updateFraultModelRuleSelective(@PathVariable("fraultModelId") String fraultModelId, 
			@PathVariable("fraultRuleId") String fraultRuleId,@RequestBody FraultModelRule fraultModelRule) {
		FraultModelRule bean =new FraultModelRule();
		bean.setFraultModelId(fraultModelId);
		bean.setFraultRuleId(fraultRuleId);
		FraultModelRule currentFraultModelRule = fraultModelRuleService.selectByPrimaryKey(bean);

		if (currentFraultModelRule == null) {
			return new ResponseRestEntity<FraultModelRule>(HttpRestStatus.NOT_FOUND);
		}
		currentFraultModelRule.setFraultModelId(fraultModelRule.getFraultModelId());
		currentFraultModelRule.setFraultRuleId(fraultModelRule.getFraultRuleId());
		fraultModelRuleService.updateByPrimaryKeySelective(currentFraultModelRule);
		//修改日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentFraultModelRule,CommonLogImpl.FRAULT_MANAGEMENT);
		return new ResponseRestEntity<FraultModelRule>(currentFraultModelRule, HttpRestStatus.OK);
	}
	
	
	@ApiOperation(value = "Delete FraultModelRule", notes = "")
	@RequestMapping(value = "/{fraultModelId}/{fraultRuleId}", method = RequestMethod.DELETE)
	public ResponseRestEntity<FraultModelRule> deleteFraultModelRule(
			@PathVariable("fraultModelId") String fraultModelId,
			@PathVariable("fraultRuleId") String fraultRuleId) {
		FraultModelRule bean = new FraultModelRule();
		bean.setFraultModelId(fraultModelId);
		bean.setFraultRuleId(fraultRuleId);
		FraultModelRule fraultModelRule = fraultModelRuleService.selectByPrimaryKey(bean);
		if (fraultModelRule == null) {
			return new ResponseRestEntity<FraultModelRule>(HttpRestStatus.NOT_FOUND);
		}
		fraultModelRuleService.deleteByPrimaryKey(fraultModelRule);
		//删除日志开始
		FraultModelRule delBean = new FraultModelRule();
		delBean.setFraultModelId(fraultModelId);
		delBean.setFraultRuleId(fraultRuleId);
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, delBean,CommonLogImpl.FRAULT_MANAGEMENT);
		//删除日志结束
		return new ResponseRestEntity<FraultModelRule>(HttpRestStatus.NO_CONTENT);
	}

}