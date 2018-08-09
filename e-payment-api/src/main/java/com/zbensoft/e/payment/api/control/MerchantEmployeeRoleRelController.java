package com.zbensoft.e.payment.api.control;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.LocaleMessageSourceService;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.service.api.MerchantEmployeeRoleRelService;
import com.zbensoft.e.payment.db.domain.MerchantEmployeeRoleRelKey;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/merchantEmployeeRoleRel")
@RestController
public class MerchantEmployeeRoleRelController {
	@Autowired
	MerchantEmployeeRoleRelService merchantEmployeeRoleRelService;
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	// 查询用户应用，支持分页
	@PreAuthorize("hasRole('R_SELLER_E_Q') or hasRole('MERCHANT')")
	@ApiOperation(value = "Query MerchantEmployeeRoleRel, support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<MerchantEmployeeRoleRelKey>> selectPage(@RequestParam(required = false) String id,
			@RequestParam(required = false) String roleId, 
			@RequestParam(required = false) String start, @RequestParam(required = false) String length) {
		MerchantEmployeeRoleRelKey merchantEmployeeRoleRelKey = new MerchantEmployeeRoleRelKey();
		merchantEmployeeRoleRelKey.setEmployeeUserId(id);
		merchantEmployeeRoleRelKey.setRoleId(roleId);
		
/*		if (start != null && length != null) {
			merchantEmployeeRoleRelKey.setPageStart(Integer.parseInt(start));
			merchantEmployeeRoleRelKey.setPageEnd(Integer.parseInt(start) + Integer.parseInt(length));
		} else {
			merchantEmployeeRoleRelKey.setPageStart(null);
			merchantEmployeeRoleRelKey.setPageEnd(null);
		}*/
		int count = merchantEmployeeRoleRelService.count(merchantEmployeeRoleRelKey);
		List<MerchantEmployeeRoleRelKey> list = merchantEmployeeRoleRelService.selectPage(merchantEmployeeRoleRelKey);
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<MerchantEmployeeRoleRelKey>>(new ArrayList<MerchantEmployeeRoleRelKey>(), HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<List<MerchantEmployeeRoleRelKey>>(list, HttpRestStatus.OK, count, count);
	}

	// 查询用户应用
	@PreAuthorize("hasRole('R_SELLER_E_Q')")
	@ApiOperation(value = "Query MerchantEmployeeRoleRel", notes = "")
	@RequestMapping(value = "/{employeeUserId}/{roleId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<MerchantEmployeeRoleRelKey> selectByPrimaryKey(@PathVariable("employeeUserId") String employeeUserId,
			@PathVariable("roleId") String roleId) {

		MerchantEmployeeRoleRelKey merchantEmployeeRoleRelKey = new MerchantEmployeeRoleRelKey();
		merchantEmployeeRoleRelKey.setEmployeeUserId(employeeUserId);
		merchantEmployeeRoleRelKey.setRoleId(roleId);
		MerchantEmployeeRoleRelKey merchantEmployeeRoleRel = merchantEmployeeRoleRelService.selectByPrimaryKey(merchantEmployeeRoleRelKey);
		if (merchantEmployeeRoleRel == null) {
			return new ResponseRestEntity<MerchantEmployeeRoleRelKey>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<MerchantEmployeeRoleRelKey>(merchantEmployeeRoleRel, HttpRestStatus.OK);
	}
	

	@PreAuthorize("hasRole('R_SELLER_E_Q')")
	@ApiOperation(value = "Query  MerchantEmployeeRoleRel", notes = "")
	@RequestMapping(value = "/{roleId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<List<MerchantEmployeeRoleRelKey>> selectByPayAppId(@PathVariable("roleId") String roleId) {
		List<MerchantEmployeeRoleRelKey> list = merchantEmployeeRoleRelService.selectByUserId(roleId);
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<MerchantEmployeeRoleRelKey>>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<List<MerchantEmployeeRoleRelKey>>(list, HttpRestStatus.OK);
	}

	// 新增用户应用
	@PreAuthorize("hasRole('R_SELLER_E_E')")
	@ApiOperation(value = "Add MerchantEmployeeRoleRel", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createMerchantEmployeeRoleRel(@RequestBody MerchantEmployeeRoleRelKey merchantEmployeeRoleRel,
			UriComponentsBuilder ucBuilder) {

	
		merchantEmployeeRoleRelService.insert(merchantEmployeeRoleRel);

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/merchantEmployeeRoleRel/{id}").buildAndExpand(merchantEmployeeRoleRel.getEmployeeUserId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,localeMessageSourceService.getMessage("common.create.created.message"));
	}

	// 修改用户应用信息
	@PreAuthorize("hasRole('R_SELLER_E_E')")
	@ApiOperation(value = "Edit MerchantEmployeeRoleRel", notes = "")
	@RequestMapping(value = "/{employeeUserId}/{roleId}", method = RequestMethod.PUT)
	public ResponseRestEntity<MerchantEmployeeRoleRelKey> updateApp(@PathVariable("employeeUserId") String employeeUserId,
			@PathVariable("roleId") String roleId, @RequestBody MerchantEmployeeRoleRelKey merchantEmployeeRoleRel) {
		MerchantEmployeeRoleRelKey merchantEmployeeRoleRelKey = new MerchantEmployeeRoleRelKey();
		merchantEmployeeRoleRelKey.setEmployeeUserId(employeeUserId);
		merchantEmployeeRoleRelKey.setRoleId(roleId);
		MerchantEmployeeRoleRelKey currentMerchantEmployeeRoleRel = merchantEmployeeRoleRelService.selectByPrimaryKey(merchantEmployeeRoleRelKey);

		if (currentMerchantEmployeeRoleRel == null) {
			return new ResponseRestEntity<MerchantEmployeeRoleRelKey>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}

		currentMerchantEmployeeRoleRel.setEmployeeUserId(merchantEmployeeRoleRel.getEmployeeUserId());
		currentMerchantEmployeeRoleRel.setRoleId(merchantEmployeeRoleRel.getRoleId());
		merchantEmployeeRoleRelService.updateByPrimaryKey(currentMerchantEmployeeRoleRel);

		return new ResponseRestEntity<MerchantEmployeeRoleRelKey>(currentMerchantEmployeeRoleRel, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	// 修改部分用户应用信息
	@PreAuthorize("hasRole('R_SELLER_E_E')")
	@ApiOperation(value = "Edit part MerchantEmployeeRoleRel", notes = "")
	@RequestMapping(value = "/{employeeUserId}/{roleId}", method = RequestMethod.PATCH)
	public ResponseRestEntity<MerchantEmployeeRoleRelKey> updateAppSelective(@PathVariable("employeeUserId") String employeeUserId,
			@PathVariable("roleId") String roleId, @RequestBody MerchantEmployeeRoleRelKey merchantEmployeeRoleRel) {
		MerchantEmployeeRoleRelKey merchantEmployeeRoleRelKey = new MerchantEmployeeRoleRelKey();
		merchantEmployeeRoleRelKey.setEmployeeUserId(employeeUserId);
		merchantEmployeeRoleRelKey.setRoleId(roleId);
		MerchantEmployeeRoleRelKey currentMerchantEmployeeRoleRel = merchantEmployeeRoleRelService.selectByPrimaryKey(merchantEmployeeRoleRelKey);

		if (currentMerchantEmployeeRoleRel == null) {
			return new ResponseRestEntity<MerchantEmployeeRoleRelKey>(HttpRestStatus.NOT_FOUND);
		}
		merchantEmployeeRoleRel.setEmployeeUserId(employeeUserId);
		merchantEmployeeRoleRel.setRoleId(roleId);
		merchantEmployeeRoleRelService.updateByPrimaryKeySelective(merchantEmployeeRoleRel);

		return new ResponseRestEntity<MerchantEmployeeRoleRelKey>(currentMerchantEmployeeRoleRel, HttpRestStatus.OK);
	}

	// 删除指定用户应用
	@PreAuthorize("hasRole('R_SELLER_E_E')")
	@ApiOperation(value = "Delete MerchantEmployeeRoleRel", notes = "")
	@RequestMapping(value = "/{employeeUserId}/{roleId}", method = RequestMethod.DELETE)
	public ResponseRestEntity<MerchantEmployeeRoleRelKey> deleteMerchantEmployeeRoleRel(@PathVariable("employeeUserId") String employeeUserId,
			@PathVariable("roleId") String roleId) {

		MerchantEmployeeRoleRelKey merchantEmployeeRoleRelKey = new MerchantEmployeeRoleRelKey();
		merchantEmployeeRoleRelKey.setEmployeeUserId(employeeUserId);
		merchantEmployeeRoleRelKey.setRoleId(roleId);
		MerchantEmployeeRoleRelKey merchantEmployeeRoleRel = merchantEmployeeRoleRelService.selectByPrimaryKey(merchantEmployeeRoleRelKey);
		if (merchantEmployeeRoleRel == null) {
			return new ResponseRestEntity<MerchantEmployeeRoleRelKey>(HttpRestStatus.NOT_FOUND);
		}

		merchantEmployeeRoleRelService.deleteByPrimaryKey(merchantEmployeeRoleRelKey);
		return new ResponseRestEntity<MerchantEmployeeRoleRelKey>(HttpRestStatus.NO_CONTENT);
	}

}
