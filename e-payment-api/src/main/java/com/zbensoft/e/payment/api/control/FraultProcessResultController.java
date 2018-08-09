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
import com.zbensoft.e.payment.api.service.api.CouponService;
import com.zbensoft.e.payment.api.service.api.FraultProcessResultService;
import com.zbensoft.e.payment.db.domain.FraultProcessResult;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/fraultProcessResult")
@RestController
public class FraultProcessResultController {
	@Autowired
	FraultProcessResultService fraultProcessResultService;
	
	@Autowired
	CouponService couponService;
	
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	@ApiOperation(value = "Query FraultProcessResult，Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<FraultProcessResult>> selectPage(
			@RequestParam(required = false) String fraultProcessId,
			@RequestParam(required = false) String fraultInfoId,
			@RequestParam(required = false) Integer processResultCode,
			@RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		FraultProcessResult fraultProcessResult = new FraultProcessResult();
		fraultProcessResult.setFraultProcessId(fraultProcessId);
		fraultProcessResult.setFraultInfoId(fraultInfoId);
		fraultProcessResult.setProcessResultCode(processResultCode);
		int count = fraultProcessResultService.count(fraultProcessResult);
		if (count == 0) {
			return new ResponseRestEntity<List<FraultProcessResult>>(new ArrayList<FraultProcessResult>(), HttpRestStatus.NOT_FOUND);
		}
		List<FraultProcessResult> list = null;
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = fraultProcessResultService.selectPage(fraultProcessResult);

		} else {
			list = fraultProcessResultService.selectPage(fraultProcessResult);
		}
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<FraultProcessResult>>(new ArrayList<FraultProcessResult>(),HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<List<FraultProcessResult>>(list, HttpRestStatus.OK, count, count);
	}

	@ApiOperation(value = "Query FraultProcessResult", notes = "")
	@RequestMapping(value = "/{fraultProcessId}/{fraultInfoId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<FraultProcessResult> selectByPrimaryKey(@PathVariable("fraultProcessId") String fraultProcessId,
			@PathVariable("fraultInfoId") String fraultInfoId) {
		FraultProcessResult bean = new FraultProcessResult();
		bean.setFraultProcessId(fraultProcessId);
		bean.setFraultInfoId(fraultInfoId);
		FraultProcessResult fraultProcessResult = fraultProcessResultService.selectByPrimaryKey(bean);
		if (fraultProcessResult == null) {
			return new ResponseRestEntity<FraultProcessResult>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<FraultProcessResult>(fraultProcessResult, HttpRestStatus.OK);
	}

	@ApiOperation(value = "Add FraultProcessResult", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createFraultProcessResult(@Valid @RequestBody FraultProcessResult fraultProcessResult,BindingResult result, UriComponentsBuilder ucBuilder) {
		
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}
		FraultProcessResult bean = fraultProcessResultService.selectByPrimaryKey(fraultProcessResult);
		if(bean !=null){
			return new ResponseRestEntity<Void>(HttpRestStatus.CONFLICT,localeMessageSourceService.getMessage("common.create.conflict.message"));
		}
		fraultProcessResult.setFraultProcessId(IDGenerate.generateCommTwo(IDGenerate.FRAULT_PROCESS_RESULT));
		fraultProcessResultService.insert(fraultProcessResult);
		//新增日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, fraultProcessResult,CommonLogImpl.FRAULT_MANAGEMENT);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/fraultProcessResult/{id}").buildAndExpand(fraultProcessResult.getFraultInfoId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,localeMessageSourceService.getMessage("common.create.created.message"));
	}

	@ApiOperation(value = "Edit FraultProcessResult", notes = "")
	@RequestMapping(value = "/{fraultProcessId}/{fraultInfoId}", method = RequestMethod.PUT)
	public ResponseRestEntity<FraultProcessResult> updateFraultProcessResult(@PathVariable("fraultProcessId") String fraultProcessId,
			@PathVariable("fraultInfoId") String fraultInfoId,@Valid @RequestBody FraultProcessResult fraultProcessResult, BindingResult result) {
		FraultProcessResult bean = new FraultProcessResult();
		bean.setFraultProcessId(fraultProcessId);
		bean.setFraultInfoId(fraultInfoId);
		FraultProcessResult currentFraultProcessResult = fraultProcessResultService.selectByPrimaryKey(bean);

		if (currentFraultProcessResult == null) {
			return new ResponseRestEntity<FraultProcessResult>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}

		currentFraultProcessResult.setFraultProcessId(fraultProcessResult.getFraultProcessId());
		currentFraultProcessResult.setFraultInfoId(fraultProcessResult.getFraultInfoId());
		currentFraultProcessResult.setProcessResultCode(fraultProcessResult.getProcessResultCode());
		currentFraultProcessResult.setProcessResultMessage(fraultProcessResult.getProcessResultMessage());
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			for (ObjectError error : list) {
				//System.out.println(error.getCode() + "---" + error.getArguments() + "---" + error.getDefaultMessage());
			}

			return new ResponseRestEntity<FraultProcessResult>(currentFraultProcessResult,HttpRestStatusFactory.createStatus(list),HttpRestStatusFactory.createStatusMessage(list));
		}
		fraultProcessResultService.updateByPrimaryKey(currentFraultProcessResult);
		//修改日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentFraultProcessResult,CommonLogImpl.FRAULT_MANAGEMENT);
		return new ResponseRestEntity<FraultProcessResult>(currentFraultProcessResult, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	@ApiOperation(value = "Edit Part FraultProcessResult", notes = "")
	@RequestMapping(value = "/{fraultProcessId}/{fraultInfoId}", method = RequestMethod.PATCH)
	public ResponseRestEntity<FraultProcessResult> updateFraultProcessResultSelective(@PathVariable("fraultProcessId") String fraultProcessId,
			@PathVariable("fraultInfoId") String fraultInfoId,
			@RequestBody FraultProcessResult fraultProcessResult) {

		FraultProcessResult bean = new FraultProcessResult();
		bean.setFraultProcessId(fraultProcessId);
		bean.setFraultInfoId(fraultInfoId);
		FraultProcessResult currentFraultProcessResult = fraultProcessResultService.selectByPrimaryKey(bean);

		if (currentFraultProcessResult == null) {
			return new ResponseRestEntity<FraultProcessResult>(HttpRestStatus.NOT_FOUND);
		}
		currentFraultProcessResult.setFraultProcessId(fraultProcessResult.getFraultProcessId());
		currentFraultProcessResult.setFraultInfoId(fraultProcessResult.getFraultInfoId());
		currentFraultProcessResult.setProcessResultCode(fraultProcessResult.getProcessResultCode());
		currentFraultProcessResult.setProcessResultMessage(fraultProcessResult.getProcessResultMessage());
		fraultProcessResultService.updateByPrimaryKeySelective(currentFraultProcessResult);
		//修改日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentFraultProcessResult,CommonLogImpl.FRAULT_MANAGEMENT);
		return new ResponseRestEntity<FraultProcessResult>(currentFraultProcessResult, HttpRestStatus.OK);
	}

	@ApiOperation(value = "Delete FraultProcessResult", notes = "")
	@RequestMapping(value = "/{fraultProcessId}/{fraultInfoId}", method = RequestMethod.DELETE)
	public ResponseRestEntity<FraultProcessResult> deleteFraultProcessResult(@PathVariable("fraultProcessId") String fraultProcessId,
			@PathVariable("fraultInfoId") String fraultInfoId) {
		FraultProcessResult bean = new FraultProcessResult();
		bean.setFraultProcessId(fraultProcessId);
		bean.setFraultInfoId(fraultInfoId);
		FraultProcessResult fraultProcessResult = fraultProcessResultService.selectByPrimaryKey(bean);
		if (fraultProcessResult == null) {
			return new ResponseRestEntity<FraultProcessResult>(HttpRestStatus.NOT_FOUND);
		}

		fraultProcessResultService.deleteByPrimaryKey(bean);
		//删除日志开始
		FraultProcessResult delBean = new FraultProcessResult();
		delBean.setFraultProcessId(fraultProcessId);
		delBean.setFraultInfoId(fraultInfoId);
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, delBean,CommonLogImpl.FRAULT_MANAGEMENT);
		//删除日志结束
		return new ResponseRestEntity<FraultProcessResult>(HttpRestStatus.NO_CONTENT);
	}
}