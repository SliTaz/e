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
import com.zbensoft.e.payment.api.service.api.FraultProcessInfoService;
import com.zbensoft.e.payment.db.domain.FraultProcessInfo;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/fraultProcessInfo")
@RestController
public class FraultProcessInfoController {
	@Autowired
	FraultProcessInfoService fraultProcessInfoService;
	
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	@ApiOperation(value = "Query FraultProcessInfo，Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<FraultProcessInfo>> selectPage(@RequestParam(required = false) String id,
			@RequestParam(required = false) String fraultInfoId,
			@RequestParam(required = false) String createTimeStart,
			@RequestParam(required = false) String createTimeEnd,
			@RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		FraultProcessInfo fraultProcessInfo = new FraultProcessInfo();
		fraultProcessInfo.setFraultProcessInfoId(id);
		fraultProcessInfo.setFraultInfoId(fraultInfoId);
		fraultProcessInfo.setCreateTimeStart(createTimeStart);
		fraultProcessInfo.setCreateTimeEnd(createTimeEnd);
		int count = fraultProcessInfoService.count(fraultProcessInfo);
		if (count == 0) {
			return new ResponseRestEntity<List<FraultProcessInfo>>(new ArrayList<FraultProcessInfo>(), HttpRestStatus.NOT_FOUND);
		}
		List<FraultProcessInfo> list = null;
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = fraultProcessInfoService.selectPage(fraultProcessInfo);
			// System.out.println("pageNum:"+pageNum+";pageSize:"+pageSize);
			// System.out.println("list.size:"+list.size());

		} else {
			list = fraultProcessInfoService.selectPage(fraultProcessInfo);
		}
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<FraultProcessInfo>>(new ArrayList<FraultProcessInfo>(),HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<List<FraultProcessInfo>>(list, HttpRestStatus.OK, count, count);
	}

	@ApiOperation(value = "Query FraultProcessInfo", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<FraultProcessInfo> selectByPrimaryKey(@PathVariable("id") String id) {
		FraultProcessInfo fraultProcessInfo = fraultProcessInfoService.selectByPrimaryKey(id);
		if (fraultProcessInfo == null) {
			return new ResponseRestEntity<FraultProcessInfo>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<FraultProcessInfo>(fraultProcessInfo, HttpRestStatus.OK);
	}

	@ApiOperation(value = "Add FraultProcessInfo", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createFraultProcessInfo(@Valid @RequestBody FraultProcessInfo fraultProcessInfo,BindingResult result, UriComponentsBuilder ucBuilder) {

		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}
		fraultProcessInfo.setFraultProcessInfoId(IDGenerate.generateCommTwo(IDGenerate.FRAULT_PROCESS_INFO));
		fraultProcessInfo.setCreateTime(PageHelperUtil.getCurrentDate());
		fraultProcessInfoService.insert(fraultProcessInfo);
		//新增日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, fraultProcessInfo,CommonLogImpl.FRAULT_MANAGEMENT);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/fraultProcessInfo/{id}").buildAndExpand(fraultProcessInfo.getFraultProcessInfoId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,localeMessageSourceService.getMessage("common.create.created.message"));
	}

	@ApiOperation(value = "Edit FraultProcessInfo", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<FraultProcessInfo> updateFraultProcessInfo(@PathVariable("id") String id,@Valid @RequestBody FraultProcessInfo fraultProcessInfo, BindingResult result) {

		FraultProcessInfo currentFraultProcessInfo = fraultProcessInfoService.selectByPrimaryKey(id);

		if (currentFraultProcessInfo == null) {
			return new ResponseRestEntity<FraultProcessInfo>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}
		currentFraultProcessInfo.setFraultInfoId(fraultProcessInfo.getFraultInfoId());
		currentFraultProcessInfo.setContent(fraultProcessInfo.getContent());
		currentFraultProcessInfo.setCreateTime(fraultProcessInfo.getCreateTime());
		currentFraultProcessInfo.setUserId(fraultProcessInfo.getUserId());
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			for (ObjectError error : list) {
				//System.out.println(error.getCode() + "---" + error.getArguments() + "---" + error.getDefaultMessage());
			}

			return new ResponseRestEntity<FraultProcessInfo>(currentFraultProcessInfo,HttpRestStatusFactory.createStatus(list),HttpRestStatusFactory.createStatusMessage(list));
		}
		fraultProcessInfoService.updateByPrimaryKey(currentFraultProcessInfo);
		//修改日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentFraultProcessInfo,CommonLogImpl.FRAULT_MANAGEMENT);
		return new ResponseRestEntity<FraultProcessInfo>(currentFraultProcessInfo, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	@ApiOperation(value = "Edit Part FraultProcessInfo", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<FraultProcessInfo> updateFraultProcessInfoSelective(@PathVariable("id") String id,
			@RequestBody FraultProcessInfo fraultProcessInfo) {

		FraultProcessInfo currentFraultProcessInfo = fraultProcessInfoService.selectByPrimaryKey(id);

		if (currentFraultProcessInfo == null) {
			return new ResponseRestEntity<FraultProcessInfo>(HttpRestStatus.NOT_FOUND);
		}
		currentFraultProcessInfo.setFraultProcessInfoId(id);
		currentFraultProcessInfo.setFraultInfoId(fraultProcessInfo.getFraultInfoId());
		currentFraultProcessInfo.setContent(fraultProcessInfo.getContent());
		currentFraultProcessInfo.setCreateTime(fraultProcessInfo.getCreateTime());
		currentFraultProcessInfo.setUserId(fraultProcessInfo.getUserId());
		fraultProcessInfoService.updateByPrimaryKeySelective(currentFraultProcessInfo);
		//修改日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentFraultProcessInfo,CommonLogImpl.FRAULT_MANAGEMENT);
		return new ResponseRestEntity<FraultProcessInfo>(currentFraultProcessInfo, HttpRestStatus.OK);
	}

	@ApiOperation(value = "Delete FraultProcessInfo", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<FraultProcessInfo> deleteFraultProcessInfo(@PathVariable("id") String id) {

		FraultProcessInfo fraultProcessInfo = fraultProcessInfoService.selectByPrimaryKey(id);
		if (fraultProcessInfo == null) {
			return new ResponseRestEntity<FraultProcessInfo>(HttpRestStatus.NOT_FOUND);
		}

		fraultProcessInfoService.deleteByPrimaryKey(id);
		//删除日志开始
		FraultProcessInfo delBean = new FraultProcessInfo();
		delBean.setFraultProcessInfoId(id);              
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, delBean,CommonLogImpl.FRAULT_MANAGEMENT);
		//删除日志结束
		return new ResponseRestEntity<FraultProcessInfo>(HttpRestStatus.NO_CONTENT);
	}

}