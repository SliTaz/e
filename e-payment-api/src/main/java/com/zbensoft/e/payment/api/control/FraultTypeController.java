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
import com.zbensoft.e.payment.api.service.api.FraultTypeService;
import com.zbensoft.e.payment.db.domain.FraultType;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/fraultType")
@RestController
public class FraultTypeController {
	@Autowired
	FraultTypeService fraultTypeService;
	
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	@ApiOperation(value = "Query FraultType，Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<FraultType>> selectPage(@RequestParam(required = false) String id,
			@RequestParam(required = false) String name) {
		FraultType fraultType = new FraultType();
		fraultType.setHandleClass(id);;
		List<FraultType> list = fraultTypeService.selectPage(fraultType);
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<FraultType>>(new ArrayList<FraultType>(),HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<List<FraultType>>(list, HttpRestStatus.OK);
	}

	@ApiOperation(value = "Query FraultType", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<FraultType> selectByPrimaryKey(@PathVariable("id") String id) {
		FraultType fraultType = fraultTypeService.selectByPrimaryKey(id);
		if (fraultType == null) {
			return new ResponseRestEntity<FraultType>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<FraultType>(fraultType, HttpRestStatus.OK);
	}

	@ApiOperation(value = "Add FraultType", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createFraultType(@Valid @RequestBody FraultType fraultType, BindingResult result, UriComponentsBuilder ucBuilder) {
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}
		FraultType bean = fraultTypeService.selectByPrimaryKey(fraultType.getFraultTypeId());
		if (fraultTypeService.isFraultTypeExist(fraultType) ||bean !=null) {
			return new ResponseRestEntity<Void>(HttpRestStatus.CONFLICT,localeMessageSourceService.getMessage("common.create.conflict.message"));
		}

		fraultTypeService.insert(fraultType);
		//新增日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, fraultType,CommonLogImpl.FRAULT_MANAGEMENT);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/fraultType/{id}").buildAndExpand(fraultType.getFraultTypeId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,localeMessageSourceService.getMessage("common.create.created.message"));
	}

	@ApiOperation(value = "Edit FraultType", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<FraultType> updateFraultType(@PathVariable("id") String id, @Valid @RequestBody FraultType fraultType, BindingResult result) {

		FraultType currentFraultType = fraultTypeService.selectByPrimaryKey(id);

		if (currentFraultType == null) {
			return new ResponseRestEntity<FraultType>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}
		currentFraultType.setFraultTypeId(fraultType.getFraultTypeId());
		currentFraultType.setName(fraultType.getName());
		currentFraultType.setHandleClass(fraultType.getHandleClass());
		currentFraultType.setStatus(fraultType.getStatus());
		currentFraultType.setRemark(fraultType.getRemark());
		
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			for (ObjectError error : list) {
				//System.out.println(error.getCode() + "---" + error.getArguments() + "---" + error.getDefaultMessage());
			}

			return new ResponseRestEntity<FraultType>(currentFraultType,HttpRestStatusFactory.createStatus(list),HttpRestStatusFactory.createStatusMessage(list));
		}
		fraultTypeService.updateByPrimaryKey(currentFraultType);
		//修改日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentFraultType,CommonLogImpl.FRAULT_MANAGEMENT);
		return new ResponseRestEntity<FraultType>(currentFraultType, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	@ApiOperation(value = "Edit Part FraultType", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<FraultType> updateFraultTypeSelective(@PathVariable("id") String id, @RequestBody FraultType fraultType) {

		FraultType currentFraultType = fraultTypeService.selectByPrimaryKey(id);

		if (currentFraultType == null) {
			return new ResponseRestEntity<FraultType>(HttpRestStatus.NOT_FOUND);
		}
		currentFraultType.setFraultTypeId(fraultType.getFraultTypeId());
		currentFraultType.setName(fraultType.getName());
		currentFraultType.setHandleClass(fraultType.getHandleClass());
		currentFraultType.setStatus(fraultType.getStatus());
		currentFraultType.setRemark(fraultType.getRemark());
		fraultTypeService.updateByPrimaryKeySelective(currentFraultType);
		//修改日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentFraultType,CommonLogImpl.FRAULT_MANAGEMENT);
		return new ResponseRestEntity<FraultType>(currentFraultType, HttpRestStatus.OK);
	}

	@ApiOperation(value = "Delete FraultType", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<FraultType> deleteFraultType(@PathVariable("id") String id) {

		FraultType fraultType = fraultTypeService.selectByPrimaryKey(id);
		if (fraultType == null) {
			return new ResponseRestEntity<FraultType>(HttpRestStatus.NOT_FOUND);
		}
		fraultTypeService.deleteByPrimaryKey(id);
		//删除日志开始
		FraultType delBean = new FraultType();
		delBean.setFraultTypeId(id);              
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, delBean,CommonLogImpl.FRAULT_MANAGEMENT);
		//删除日志结束
		return new ResponseRestEntity<FraultType>(HttpRestStatus.NO_CONTENT);
	}

}