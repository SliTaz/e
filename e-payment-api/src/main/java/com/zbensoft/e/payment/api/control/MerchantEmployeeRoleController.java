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
import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.HttpRestStatusFactory;
import com.zbensoft.e.payment.api.common.LocaleMessageSourceService;
import com.zbensoft.e.payment.api.common.PageHelperUtil;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.service.api.MerchantEmployeeRoleService;
import com.zbensoft.e.payment.db.domain.MerchantEmployeeRole;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/merchantEmployeeRole")
@RestController
public class MerchantEmployeeRoleController {
	@Autowired
	MerchantEmployeeRoleService merchantEmployeeRoleService;
	
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	@PreAuthorize("hasRole('R_SELLER_E_Q') or hasRole('MERCHANT')")
	@ApiOperation(value = "Query MerchantEmployeeRole，Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<MerchantEmployeeRole>> selectPage(@RequestParam(required = false) String id,
			
			@RequestParam(required = false) String name,
			@RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		MerchantEmployeeRole merchantEmployeeRole = new MerchantEmployeeRole();
		merchantEmployeeRole.setRoleId(id);
	
		merchantEmployeeRole.setName(name);
		
		int count = merchantEmployeeRoleService.count(merchantEmployeeRole);
		if (count == 0) {
			return new ResponseRestEntity<List<MerchantEmployeeRole>>(new ArrayList<MerchantEmployeeRole>(), HttpRestStatus.NOT_FOUND);
		}
		List<MerchantEmployeeRole> list = null;
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = merchantEmployeeRoleService.selectPage(merchantEmployeeRole);
			// System.out.println("pageNum:"+pageNum+";pageSize:"+pageSize);
			// System.out.println("list.size:"+list.size());

		} else {
			list = merchantEmployeeRoleService.selectPage(merchantEmployeeRole);
		}
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<MerchantEmployeeRole>>(new ArrayList<MerchantEmployeeRole>(),HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<List<MerchantEmployeeRole>>(list, HttpRestStatus.OK, count, count);
	}

	@PreAuthorize("hasRole('R_SELLER_E_Q')")
	@ApiOperation(value = "Query MerchantEmployeeRole", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<MerchantEmployeeRole> selectByPrimaryKey(@PathVariable("id") String id) {
		MerchantEmployeeRole merchantEmployeeRole = merchantEmployeeRoleService.selectByPrimaryKey(id);
		if (merchantEmployeeRole == null) {
			return new ResponseRestEntity<MerchantEmployeeRole>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<MerchantEmployeeRole>(merchantEmployeeRole, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_SELLER_E_E') or hasRole('MERCHANT')")
	@ApiOperation(value = "Add MerchantEmployeeRole", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createMerchantEmployeeRole(@Valid @RequestBody MerchantEmployeeRole merchantEmployeeRole,BindingResult result, UriComponentsBuilder ucBuilder) {
		
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}
		merchantEmployeeRoleService.insert(merchantEmployeeRole);

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/merchantEmployeeRole/{id}").buildAndExpand(merchantEmployeeRole.getRoleId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,localeMessageSourceService.getMessage("common.create.created.message"));
	}

	@PreAuthorize("hasRole('R_SELLER_E_E')")
	@ApiOperation(value = "Edit MerchantEmployeeRole", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<MerchantEmployeeRole> updateMerchantEmployeeRole(@PathVariable("id") String id,@Valid @RequestBody MerchantEmployeeRole merchantEmployeeRole, BindingResult result) {

		MerchantEmployeeRole currentMerchantEmployeeRole = merchantEmployeeRoleService.selectByPrimaryKey(id);

		if (currentMerchantEmployeeRole == null) {
			return new ResponseRestEntity<MerchantEmployeeRole>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}
		
		
		currentMerchantEmployeeRole.setCode(merchantEmployeeRole.getCode());
		currentMerchantEmployeeRole.setName(merchantEmployeeRole.getName());
		currentMerchantEmployeeRole.setRemark(merchantEmployeeRole.getRemark());
		
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			for (ObjectError error : list) {
				//System.out.println(error.getCode() + "---" + error.getArguments() + "---" + error.getDefaultMessage());
			}

			return new ResponseRestEntity<MerchantEmployeeRole>(currentMerchantEmployeeRole,HttpRestStatusFactory.createStatus(list),HttpRestStatusFactory.createStatusMessage(list));
		}
		merchantEmployeeRoleService.updateByPrimaryKey(currentMerchantEmployeeRole);

		return new ResponseRestEntity<MerchantEmployeeRole>(currentMerchantEmployeeRole, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	@PreAuthorize("hasRole('R_SELLER_E_E')")
	@ApiOperation(value = "Edit Part MerchantEmployeeRole", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<MerchantEmployeeRole> updateMerchantEmployeeRoleSelective(@PathVariable("id") String id,
			@RequestBody MerchantEmployeeRole merchantEmployeeRole) {

		MerchantEmployeeRole currentMerchantEmployeeRole = merchantEmployeeRoleService.selectByPrimaryKey(id);

		if (currentMerchantEmployeeRole == null) {
			return new ResponseRestEntity<MerchantEmployeeRole>(HttpRestStatus.NOT_FOUND);
		}
		merchantEmployeeRole.setRoleId(id);
		merchantEmployeeRoleService.updateByPrimaryKeySelective(merchantEmployeeRole);

		return new ResponseRestEntity<MerchantEmployeeRole>(currentMerchantEmployeeRole, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_SELLER_E_E')")
	@ApiOperation(value = "Delete MerchantEmployeeRole", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<MerchantEmployeeRole> deleteMerchantEmployeeRole(@PathVariable("id") String id) {

		MerchantEmployeeRole merchantEmployeeRole = merchantEmployeeRoleService.selectByPrimaryKey(id);
		if (merchantEmployeeRole == null) {
			return new ResponseRestEntity<MerchantEmployeeRole>(HttpRestStatus.NOT_FOUND);
		}

		merchantEmployeeRoleService.deleteByPrimaryKey(id);
		return new ResponseRestEntity<MerchantEmployeeRole>(HttpRestStatus.NO_CONTENT);
	}

}