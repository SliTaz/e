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
import com.zbensoft.e.payment.api.common.LocaleMessageSourceService;
import com.zbensoft.e.payment.api.common.PageHelperUtil;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.service.api.HelpDocTypeService;
import com.zbensoft.e.payment.db.domain.HelpDocType;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/helpDocType")
@RestController
public class HelpDocTypeController {
	
	@Autowired
	HelpDocTypeService helpDocTypeService;
	
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	// 查询应用，支持分页
	@ApiOperation(value = "Query application, support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<HelpDocType>> selectPage(@RequestParam(required = false) String helpDocTypeid,
			@RequestParam(required = false) String parentTypeId,
			@RequestParam(required = false) String typeName,
			@RequestParam(required = false) Integer deleteFlag,
			@RequestParam(required = false) String remark, @RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		
		HelpDocType helpDocType=new HelpDocType();
		helpDocType.setHelpDocTypeid(helpDocTypeid);
		helpDocType.setParentTypeId(parentTypeId);
		helpDocType.setTypeName(typeName);
		helpDocType.setDeleteFlag(0);
		
		List<HelpDocType> list =helpDocTypeService.selectPage(helpDocType);
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = helpDocTypeService.selectPage(helpDocType);
			// System.out.println("pageNum:"+pageNum+";pageSize:"+pageSize);
			// System.out.println("list.size:"+list.size());

		} else {
			list = helpDocTypeService.selectPage(helpDocType);
		}

		int count = helpDocTypeService.count(helpDocType);
		// 分页 end

		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<HelpDocType>>(new ArrayList<HelpDocType>(), HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<List<HelpDocType>>(list, HttpRestStatus.OK, count, count);

	}

	// 查询应用
	@ApiOperation(value = "Query application", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<HelpDocType> selectByPrimaryKey(@PathVariable("id") String id) {
		HelpDocType helpDocType = helpDocTypeService.selectByPrimaryKey(id);
		if (helpDocType == null) {
			return new ResponseRestEntity<HelpDocType>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<HelpDocType>(helpDocType, HttpRestStatus.OK);
	}

	// 新增应用
	@ApiOperation(value = "Add application", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createHelpDocType(@Valid @RequestBody HelpDocType helpDocType, BindingResult result,
			UriComponentsBuilder ucBuilder) {
		/*consumptionType.setBillConsumptionTypeId(System.currentTimeMillis() + "");*/
		HelpDocType bean = helpDocTypeService.selectByPrimaryKey(helpDocType.getHelpDocTypeid());
		helpDocType.setDeleteFlag(0);
		if (helpDocTypeService.isHelpDocTypeExist(helpDocType)||bean !=null) {
			return new ResponseRestEntity<Void>(HttpRestStatus.CONFLICT, localeMessageSourceService.getMessage("common.create.conflict.message"));
		}

		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}

		helpDocTypeService.insert(helpDocType);
		//新增日志
         CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, helpDocType,CommonLogImpl.FINANCE);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/helpDocType/{id}").buildAndExpand(helpDocType.getHelpDocTypeid()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED, localeMessageSourceService.getMessage("common.create.created.message"));

	}

	// 修改应用信息
	@ApiOperation(value = "Modify the application information", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<HelpDocType> updateHelpDocType(@PathVariable("id") String id, @Valid @RequestBody HelpDocType helpDocType,
			BindingResult result) {

		HelpDocType currentHelpDocType = helpDocTypeService.selectByPrimaryKey(id);

		if (currentHelpDocType == null) {
			return new ResponseRestEntity<HelpDocType>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}
		currentHelpDocType.setHelpDocTypeid(helpDocType.getHelpDocTypeid());
		currentHelpDocType.setParentTypeId(helpDocType.getParentTypeId());
		currentHelpDocType.setTypeName(helpDocType.getTypeName());
		
		
		
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			for (ObjectError error : list) {
				//System.out.println(error.getCode() + "---" + error.getArguments() + "---" + error.getDefaultMessage());
			}

			return new ResponseRestEntity<HelpDocType>(currentHelpDocType, HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}

		helpDocTypeService.updateByPrimaryKey(currentHelpDocType);
		//修改日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentHelpDocType,CommonLogImpl.FINANCE);
		return new ResponseRestEntity<HelpDocType>(currentHelpDocType, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	// 修改部分应用信息
	@ApiOperation(value = "Modify part of the application information", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<HelpDocType> updateHelpDocTypeSelective(@PathVariable("id") String id, @RequestBody HelpDocType helpDocType) {

		HelpDocType currentHelpDocType = helpDocTypeService.selectByPrimaryKey(id);

		if (currentHelpDocType == null) {
			return new ResponseRestEntity<HelpDocType>(HttpRestStatus.NOT_FOUND);
		}
		helpDocType.setParentTypeId(id);
		helpDocTypeService.updateByPrimaryKeySelective(helpDocType);
		//修改日志
	   CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, helpDocType,CommonLogImpl.FINANCE);
		return new ResponseRestEntity<HelpDocType>(helpDocType, HttpRestStatus.OK);
	}

	// 删除指定应用
	@ApiOperation(value = "Delete the specified consumptionType", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<HelpDocType> deleteHelpDocType(@PathVariable("id") String id) {

		HelpDocType helpDocType = helpDocTypeService.selectByPrimaryKey(id);
		if (helpDocType == null) {
			return new ResponseRestEntity<HelpDocType>(HttpRestStatus.NOT_FOUND);
		}

		helpDocType.setDeleteFlag(1);
		helpDocTypeService.updateByPrimaryKeySelective(helpDocType);
		//删除日志开始
		HelpDocType delBean = new HelpDocType();
		delBean.setHelpDocTypeid(id);
		delBean.setDeleteFlag(1);
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, delBean,CommonLogImpl.FINANCE);
		//删除日志结束
		return new ResponseRestEntity<HelpDocType>(HttpRestStatus.NO_CONTENT);
	}

}