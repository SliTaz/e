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
import com.zbensoft.e.payment.api.service.api.ConsumptionTypeService;
import com.zbensoft.e.payment.db.domain.ConsumptionType;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/consumptionType")
@RestController
public class ConsumptionTypeController {
	@Autowired
	ConsumptionTypeService consumptionTypeService;
	
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	// 查询应用，支持分页
	@PreAuthorize("hasRole('R_TRADE_C_T_Q') or hasRole('CONSUMER')")
	@ApiOperation(value = "Query application, support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<ConsumptionType>> selectPage(@RequestParam(required = false) String id,
			@RequestParam(required = false) String name,
			@RequestParam(required = false) Integer deleteFlag,
			@RequestParam(required = false) String remark, @RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		ConsumptionType consumptionType = new ConsumptionType();
		consumptionType.setBillConsumptionTypeId(id);
		consumptionType.setName(name);
		consumptionType.setDeleteFlag(0);
		
		consumptionType.setRemark(remark);
		List<ConsumptionType> list = consumptionTypeService.selectPage(consumptionType);
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = consumptionTypeService.selectPage(consumptionType);
			// System.out.println("pageNum:"+pageNum+";pageSize:"+pageSize);
			// System.out.println("list.size:"+list.size());

		} else {
			list = consumptionTypeService.selectPage(consumptionType);
		}

		int count = consumptionTypeService.count(consumptionType);
		// 分页 end

		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<ConsumptionType>>(new ArrayList<ConsumptionType>(), HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<List<ConsumptionType>>(list, HttpRestStatus.OK, count, count);

	}

	// 查询应用
	@PreAuthorize("hasRole('R_TRADE_C_T_Q')")
	@ApiOperation(value = "Query application", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<ConsumptionType> selectByPrimaryKey(@PathVariable("id") String id) {
		ConsumptionType consumptionType = consumptionTypeService.selectByPrimaryKey(id);
		if (consumptionType == null) {
			return new ResponseRestEntity<ConsumptionType>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<ConsumptionType>(consumptionType, HttpRestStatus.OK);
	}

	// 新增应用
	@PreAuthorize("hasRole('R_TRADE_C_T_E')")
	@ApiOperation(value = "Add application", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createConsumptionType(@Valid @RequestBody ConsumptionType consumptionType, BindingResult result,
			UriComponentsBuilder ucBuilder) {
		/*consumptionType.setBillConsumptionTypeId(System.currentTimeMillis() + "");*/
		ConsumptionType bean = consumptionTypeService.selectByPrimaryKey(consumptionType.getBillConsumptionTypeId());
		consumptionType.setDeleteFlag(0);
		if (consumptionTypeService.isConsumptionTypeExist(consumptionType)||bean !=null) {
			return new ResponseRestEntity<Void>(HttpRestStatus.CONFLICT, localeMessageSourceService.getMessage("common.create.conflict.message"));
		}

		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}

		consumptionTypeService.insert(consumptionType);
		//新增日志
         CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, consumptionType,CommonLogImpl.FINANCE);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/consumptionType/{id}").buildAndExpand(consumptionType.getBillConsumptionTypeId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED, localeMessageSourceService.getMessage("common.create.created.message"));

	}

	// 修改应用信息
	@PreAuthorize("hasRole('R_TRADE_C_T_E')")
	@ApiOperation(value = "Modify the application information", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<ConsumptionType> updateConsumptionType(@PathVariable("id") String id, @Valid @RequestBody ConsumptionType consumptionType,
			BindingResult result) {

		ConsumptionType currentConsumptionType = consumptionTypeService.selectByPrimaryKey(id);

		if (currentConsumptionType == null) {
			return new ResponseRestEntity<ConsumptionType>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}
		currentConsumptionType.setBillConsumptionTypeId(consumptionType.getBillConsumptionTypeId());
		currentConsumptionType.setName(consumptionType.getName());
		currentConsumptionType.setDeleteFlag(consumptionType.getDeleteFlag());
		
		
		
		currentConsumptionType.setRemark(consumptionType.getRemark());
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			for (ObjectError error : list) {
				//System.out.println(error.getCode() + "---" + error.getArguments() + "---" + error.getDefaultMessage());
			}

			return new ResponseRestEntity<ConsumptionType>(currentConsumptionType, HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}

		consumptionTypeService.updateByPrimaryKey(currentConsumptionType);
		//修改日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentConsumptionType,CommonLogImpl.FINANCE);
		return new ResponseRestEntity<ConsumptionType>(currentConsumptionType, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	// 修改部分应用信息
	@PreAuthorize("hasRole('R_TRADE_C_T_E')")
	@ApiOperation(value = "Modify part of the application information", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<ConsumptionType> updateConsumptionTypeSelective(@PathVariable("id") String id, @RequestBody ConsumptionType consumptionType) {

		ConsumptionType currentConsumptionType = consumptionTypeService.selectByPrimaryKey(id);

		if (currentConsumptionType == null) {
			return new ResponseRestEntity<ConsumptionType>(HttpRestStatus.NOT_FOUND);
		}
		consumptionType.setBillConsumptionTypeId(id);
		consumptionTypeService.updateByPrimaryKeySelective(consumptionType);
		//修改日志
	   CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, consumptionType,CommonLogImpl.FINANCE);
		return new ResponseRestEntity<ConsumptionType>(currentConsumptionType, HttpRestStatus.OK);
	}

	// 删除指定应用
	@PreAuthorize("hasRole('R_TRADE_C_T_E')")
	@ApiOperation(value = "Delete the specified consumptionType", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<ConsumptionType> deleteConsumptionType(@PathVariable("id") String id) {

		ConsumptionType consumptionType = consumptionTypeService.selectByPrimaryKey(id);
		if (consumptionType == null) {
			return new ResponseRestEntity<ConsumptionType>(HttpRestStatus.NOT_FOUND);
		}

		consumptionType.setDeleteFlag(1);
		consumptionTypeService.updateByPrimaryKeySelective(consumptionType);
		//删除日志开始
		ConsumptionType delBean = new ConsumptionType();
		delBean.setBillConsumptionTypeId(id);
		delBean.setDeleteFlag(1);
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, delBean,CommonLogImpl.FINANCE);
		//删除日志结束
		return new ResponseRestEntity<ConsumptionType>(HttpRestStatus.NO_CONTENT);
	}

}