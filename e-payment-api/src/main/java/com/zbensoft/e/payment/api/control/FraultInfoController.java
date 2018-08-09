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
import com.zbensoft.e.payment.api.service.api.FraultInfoService;
import com.zbensoft.e.payment.api.service.api.FraultModelService;
import com.zbensoft.e.payment.db.domain.FraultInfo;
import com.zbensoft.e.payment.db.domain.FraultModel;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/fraultInfo")
@RestController
public class FraultInfoController {
	@Autowired
	FraultInfoService fraultInfoService;
	
	@Autowired
	FraultModelService fraultModelService;
	
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	@ApiOperation(value = "Query FraultInfo，Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<FraultInfo>> selectPage(@RequestParam(required = false) String id,
			@RequestParam(required = false) Integer fraultBodyType,
			@RequestParam(required = false) String fraultModelId,
			@RequestParam(required = false) Integer status,
			@RequestParam(required = false) String createTimeStart,
			@RequestParam(required = false) String createTimeEnd,
			@RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		FraultInfo fraultInfo = new FraultInfo();
		fraultInfo.setFraultInfoId(id);
		fraultInfo.setFraultModelId(fraultModelId);
		fraultInfo.setFraultBodyType(fraultBodyType);
		fraultInfo.setStatus(status);
		fraultInfo.setCreateTimeStart(createTimeStart);
		fraultInfo.setCreateTimeEnd(createTimeEnd);
		int count = fraultInfoService.count(fraultInfo);
		if (count == 0) {
			return new ResponseRestEntity<List<FraultInfo>>(new ArrayList<FraultInfo>(), HttpRestStatus.NOT_FOUND);
		}
		List<FraultInfo> list = null;
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = fraultInfoService.selectPage(fraultInfo);
			// System.out.println("pageNum:"+pageNum+";pageSize:"+pageSize);
			// System.out.println("list.size:"+list.size());

		} else {
			list = fraultInfoService.selectPage(fraultInfo);
		}
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<FraultInfo>>(new ArrayList<FraultInfo>(),HttpRestStatus.NOT_FOUND);
		}
		List<FraultInfo> listNew = new ArrayList<FraultInfo>();
		for(FraultInfo bean:list){
			FraultModel fraultModel = fraultModelService.selectByPrimaryKey(bean.getFraultModelId());
			if(fraultModel!=null){
				bean.setFraultModelName(fraultModel.getName());
			}
			listNew.add(bean);
		}
		return new ResponseRestEntity<List<FraultInfo>>(list, HttpRestStatus.OK, count, count);
	}

	@ApiOperation(value = "Query FraultInfo", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<FraultInfo> selectByPrimaryKey(@PathVariable("id") String id) {
		FraultInfo fraultInfo = fraultInfoService.selectByPrimaryKey(id);
		if (fraultInfo == null) {
			return new ResponseRestEntity<FraultInfo>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<FraultInfo>(fraultInfo, HttpRestStatus.OK);
	}

	@ApiOperation(value = "Add FraultInfo", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createFraultInfo(@Valid @RequestBody FraultInfo fraultInfo,BindingResult result, UriComponentsBuilder ucBuilder) {
		
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}
	     fraultInfo.setFraultInfoId(IDGenerate.generateCommTwo(IDGenerate.FRAULT_INFO));
	     fraultInfo.setCreateTime(PageHelperUtil.getCurrentDate());
		//fraultInfo.setFraultInfoId(IDGenerate.generateCommTwo(IDGenerate.TRAD_SEQ));
		//fraultInfo.setFraultInfoId(IDGenerate.generateCommTwo(IDGenerate.FRAULT_INFO));
		fraultInfoService.insert(fraultInfo);
		//新增日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, fraultInfo,CommonLogImpl.FRAULT_MANAGEMENT);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/fraultInfo/{id}").buildAndExpand(fraultInfo.getFraultInfoId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,localeMessageSourceService.getMessage("common.create.created.message"));
	}

	@ApiOperation(value = "Edit FraultInfo", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<FraultInfo> updateFraultInfo(@PathVariable("id") String id,@Valid @RequestBody FraultInfo fraultInfo, BindingResult result) {

		FraultInfo currentFraultInfo = fraultInfoService.selectByPrimaryKey(id);

		if (currentFraultInfo == null) {
			return new ResponseRestEntity<FraultInfo>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}

		currentFraultInfo.setFraultModelId(fraultInfo.getFraultModelId());
		currentFraultInfo.setFraultBodyType(fraultInfo.getFraultBodyType());
		currentFraultInfo.setFraultBodyContent(fraultInfo.getFraultBodyContent());
		currentFraultInfo.setContent(fraultInfo.getContent());
		currentFraultInfo.setStatus(fraultInfo.getStatus());
		currentFraultInfo.setRemark(fraultInfo.getRemark());
		currentFraultInfo.setCreateTime(fraultInfo.getCreateTime());
		
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			for (ObjectError error : list) {
				//System.out.println(error.getCode() + "---" + error.getArguments() + "---" + error.getDefaultMessage());
			}

			return new ResponseRestEntity<FraultInfo>(currentFraultInfo,HttpRestStatusFactory.createStatus(list),HttpRestStatusFactory.createStatusMessage(list));
		}
		fraultInfoService.updateByPrimaryKey(currentFraultInfo);
		//修改日志
         CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentFraultInfo,CommonLogImpl.FRAULT_MANAGEMENT);
		return new ResponseRestEntity<FraultInfo>(currentFraultInfo, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	@ApiOperation(value = "Edit Part FraultInfo", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<FraultInfo> updateFraultInfoSelective(@PathVariable("id") String id,
			@RequestBody FraultInfo fraultInfo) {

		FraultInfo currentFraultInfo = fraultInfoService.selectByPrimaryKey(id);

		if (currentFraultInfo == null) {
			return new ResponseRestEntity<FraultInfo>(HttpRestStatus.NOT_FOUND);
		}
		currentFraultInfo.setFraultInfoId(id);
		currentFraultInfo.setFraultModelId(fraultInfo.getFraultModelId());
		currentFraultInfo.setFraultBodyType(fraultInfo.getFraultBodyType());
		currentFraultInfo.setFraultBodyContent(fraultInfo.getFraultBodyContent());
		currentFraultInfo.setContent(fraultInfo.getContent());
		currentFraultInfo.setStatus(fraultInfo.getStatus());
		currentFraultInfo.setRemark(fraultInfo.getRemark());
		currentFraultInfo.setCreateTime(fraultInfo.getCreateTime());
		fraultInfoService.updateByPrimaryKeySelective(currentFraultInfo);
		//修改日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentFraultInfo,CommonLogImpl.FRAULT_MANAGEMENT);
		return new ResponseRestEntity<FraultInfo>(currentFraultInfo, HttpRestStatus.OK);
	}

	@ApiOperation(value = "Delete FraultInfo", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<FraultInfo> deleteFraultInfo(@PathVariable("id") String id) {

		FraultInfo fraultInfo = fraultInfoService.selectByPrimaryKey(id);
		if (fraultInfo == null) {
			return new ResponseRestEntity<FraultInfo>(HttpRestStatus.NOT_FOUND);
		}

		fraultInfoService.deleteByPrimaryKey(id);
		
		//删除日志开始
		FraultInfo delBean = new FraultInfo();
		delBean.setFraultInfoId(id);              
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, delBean,CommonLogImpl.FRAULT_MANAGEMENT);
		//删除日志结束
		return new ResponseRestEntity<FraultInfo>(HttpRestStatus.NO_CONTENT);
	}

}