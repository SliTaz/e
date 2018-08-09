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
import com.zbensoft.e.payment.api.service.api.FraultModelRuleService;
import com.zbensoft.e.payment.api.service.api.FraultRuleService;
import com.zbensoft.e.payment.db.domain.FraultModelRule;
import com.zbensoft.e.payment.db.domain.FraultRule;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/fraultRule")
@RestController
public class FraultRuleController {
	@Autowired
	FraultRuleService fraultRuleService;
	
	@Autowired
	FraultModelRuleService fraultModelRuleService;
	
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	@PreAuthorize("hasRole('R_FRAUD_R_Q')")
	@ApiOperation(value = "Query FraultRule，Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<FraultRule>> selectPage(@RequestParam(required = false) String id,
			@RequestParam(required = false) String name,
			@RequestParam(required = false) Integer status,
			@RequestParam(required = false) String createTimeStart,
			@RequestParam(required = false) String createTimeEnd,
			@RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		FraultRule fraultRule = new FraultRule();
		fraultRule.setFraultRuleId(id);
		fraultRule.setName(name);
		fraultRule.setStatus(status);
		fraultRule.setCreateTimeStart(createTimeStart);
		fraultRule.setCreateTimeEnd(createTimeEnd);
		int count = fraultRuleService.count(fraultRule);
		if (count == 0) {
			return new ResponseRestEntity<List<FraultRule>>(new ArrayList<FraultRule>(), HttpRestStatus.NOT_FOUND);
		}
		List<FraultRule> list = null;
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = fraultRuleService.selectPage(fraultRule);
			// System.out.println("pageNum:"+pageNum+";pageSize:"+pageSize);
			// System.out.println("list.size:"+list.size());

		} else {
			list = fraultRuleService.selectPage(fraultRule);
		}
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<FraultRule>>(new ArrayList<FraultRule>(),HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<List<FraultRule>>(list, HttpRestStatus.OK, count, count);
	}

	@PreAuthorize("hasRole('R_FRAUD_R_Q')")
	@ApiOperation(value = "Query FraultRule", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<FraultRule> selectByPrimaryKey(@PathVariable("id") String id) {
		FraultRule fraultRule = fraultRuleService.selectByPrimaryKey(id);
		if (fraultRule == null) {
			return new ResponseRestEntity<FraultRule>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<FraultRule>(fraultRule, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_FRAUD_R_E')")
	@ApiOperation(value = "Add FraultRule", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createFraultRule(@Valid @RequestBody FraultRule fraultRule,BindingResult result, UriComponentsBuilder ucBuilder) {

		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}
		fraultRule.setCreateTime(PageHelperUtil.getCurrentDate());
		fraultRuleService.insert(fraultRule);
		
		//关系表新增Start
		if(fraultRule.getFraultModel()!=null){
			String[] idStr = fraultRule.getFraultModel().split(",");
			for(int i=0;i<idStr.length;i++){
				FraultModelRule  fraultModelRule =new FraultModelRule();
				fraultModelRule.setFraultRuleId(fraultRule.getFraultRuleId());
				fraultModelRule.setFraultModelId(idStr[i]);
				fraultModelRuleService.insert(fraultModelRule);
			}
		}
		//关系表新增End
		//新增日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, fraultRule,CommonLogImpl.FRAULT_MANAGEMENT);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/fraultRule/{id}").buildAndExpand(fraultRule.getFraultRuleId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,localeMessageSourceService.getMessage("common.create.created.message"));
	}

	@PreAuthorize("hasRole('R_FRAUD_R_E')")
	@ApiOperation(value = "Edit FraultRule", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<FraultRule> updateFraultRule(@PathVariable("id") String id,@Valid @RequestBody FraultRule fraultRule, BindingResult result) {

		FraultRule currentFraultRule = fraultRuleService.selectByPrimaryKey(id);
		List<FraultModelRule> fraultModelRuleList =fraultModelRuleService.selectByRuleId(id);

		if (currentFraultRule == null) {
			return new ResponseRestEntity<FraultRule>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}
		currentFraultRule.setName(fraultRule.getName());
		currentFraultRule.setStatus(fraultRule.getStatus());
		currentFraultRule.setHandleClass(fraultRule.getHandleClass());
		currentFraultRule.setCreateTime(fraultRule.getCreateTime());
		currentFraultRule.setRemark(fraultRule.getRemark());
		
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			for (ObjectError error : list) {
				//System.out.println(error.getCode() + "---" + error.getArguments() + "---" + error.getDefaultMessage());
			}

			return new ResponseRestEntity<FraultRule>(currentFraultRule,HttpRestStatusFactory.createStatus(list),HttpRestStatusFactory.createStatusMessage(list));
		}
		fraultRuleService.updateByPrimaryKey(currentFraultRule);
		
		//关系表修改start(逻辑:先删除，然后增加)
		if (fraultModelRuleList != null && fraultModelRuleList.size() > 0) {
			for(FraultModelRule gateway :fraultModelRuleList){
				fraultModelRuleService.deleteByPrimaryKey(gateway);
			}
		}
		
		if(fraultRule.getFraultModel()!=null&&!"".equals(fraultRule.getFraultModel())){
			String[] idStr = fraultRule.getFraultModel().split(",");
			for(int i=0;i<idStr.length;i++){
				FraultModelRule  fraultModelRule =new FraultModelRule();
				fraultModelRule.setFraultRuleId(fraultRule.getFraultRuleId());
				fraultModelRule.setFraultModelId(idStr[i]);
				fraultModelRuleService.insert(fraultModelRule);
			}
		}
		//关系表修改start(逻辑:先删除，然后增加)
		//修改日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentFraultRule,CommonLogImpl.FRAULT_MANAGEMENT);
		return new ResponseRestEntity<FraultRule>(currentFraultRule, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	@PreAuthorize("hasRole('R_FRAUD_R_E')")
	@ApiOperation(value = "Edit Part FraultRule", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<FraultRule> updateFraultRuleSelective(@PathVariable("id") String id,
			@RequestBody FraultRule fraultRule) {

		FraultRule currentFraultRule = fraultRuleService.selectByPrimaryKey(id);

		if (currentFraultRule == null) {
			return new ResponseRestEntity<FraultRule>(HttpRestStatus.NOT_FOUND);
		}
		currentFraultRule.setFraultRuleId(id);
		currentFraultRule.setName(fraultRule.getName());
		currentFraultRule.setStatus(fraultRule.getStatus());
		currentFraultRule.setHandleClass(fraultRule.getHandleClass());
		currentFraultRule.setCreateTime(fraultRule.getCreateTime());
		currentFraultRule.setRemark(fraultRule.getRemark());
		fraultRuleService.updateByPrimaryKeySelective(currentFraultRule);
		//修改日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentFraultRule,CommonLogImpl.FRAULT_MANAGEMENT);
		return new ResponseRestEntity<FraultRule>(currentFraultRule, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_FRAUD_R_E')")
	@ApiOperation(value = "Delete FraultRule", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<FraultRule> deleteFraultRule(@PathVariable("id") String id) {

		FraultRule fraultRule = fraultRuleService.selectByPrimaryKey(id);
		List<FraultModelRule> fraultModelRuleList =fraultModelRuleService.selectByRuleId(id);
		if (fraultRule == null) {
			return new ResponseRestEntity<FraultRule>(HttpRestStatus.NOT_FOUND);
		}

		fraultRuleService.deleteByPrimaryKey(id);
		
		//关系表删除start
		if (fraultModelRuleList != null && fraultModelRuleList.size()>0){
			for(FraultModelRule modelRule :fraultModelRuleList){
				fraultModelRuleService.deleteByPrimaryKey(modelRule);
			}
		}
		//关系表删除end
		//删除日志开始
		FraultRule delBean = new FraultRule();
		delBean.setFraultRuleId(id);              
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, delBean,CommonLogImpl.FRAULT_MANAGEMENT);
		//删除日志结束
		return new ResponseRestEntity<FraultRule>(HttpRestStatus.NO_CONTENT);
	}

}