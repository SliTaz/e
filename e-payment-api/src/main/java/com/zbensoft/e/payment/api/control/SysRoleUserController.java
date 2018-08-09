package com.zbensoft.e.payment.api.control;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
import com.zbensoft.e.payment.api.service.api.SysRoleUserService;
import com.zbensoft.e.payment.db.domain.SysRoleUserKey;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/sysRoleUser")
@RestController
public class SysRoleUserController {
	@Autowired
	SysRoleUserService sysRoleUserService;
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;


	// 查询用户应用，支持分页
	@PreAuthorize("hasRole('R_MANAG_U_Q')")
	@ApiOperation(value = "Query SysRoleUser,Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<SysRoleUserKey>> selectPage(@RequestParam(required = false) String id,
			@RequestParam(required = false) String userId, 
			@RequestParam(required = false) String start, @RequestParam(required = false) String length) {
		SysRoleUserKey sysRoleUserKey = new SysRoleUserKey();
		sysRoleUserKey.setRoleId(id);
		sysRoleUserKey.setUserId(userId);
		
		int count = sysRoleUserService.count(sysRoleUserKey);
		List<SysRoleUserKey> list = sysRoleUserService.selectPage(sysRoleUserKey);
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<SysRoleUserKey>>(new ArrayList<SysRoleUserKey>(), HttpRestStatus.NOT_FOUND);
		}

		return new ResponseRestEntity<List<SysRoleUserKey>>(list, HttpRestStatus.OK, count, count);
	}

	

	// 查询用户应用
	@PreAuthorize("hasRole('R_MANAG_U_Q')")
	@ApiOperation(value = "Query SysRoleUser", notes = "")
	@RequestMapping(value = "/{roleId}/{userId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<SysRoleUserKey> selectByPrimaryKey(@PathVariable("roleId") String roleId,
			@PathVariable("userId") String userId) {

		SysRoleUserKey sysRoleUserKey = new SysRoleUserKey();
		sysRoleUserKey.setRoleId(roleId);
		sysRoleUserKey.setUserId(userId);
		SysRoleUserKey sysRoleUser = sysRoleUserService.selectByPrimaryKey(sysRoleUserKey);
		if (sysRoleUser == null) {
			return new ResponseRestEntity<SysRoleUserKey>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<SysRoleUserKey>(sysRoleUser, HttpRestStatus.OK);
	}


	@PreAuthorize("hasRole('R_MANAG_U_Q')")
	@ApiOperation(value = "Query SysRoleUser by userId", notes = "")
	@RequestMapping(value = "/{userId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<List<SysRoleUserKey>> selectByUserId(@PathVariable("userId") String userId) {
		List<SysRoleUserKey> list = sysRoleUserService.selectByUserId(userId);
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<SysRoleUserKey>>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<List<SysRoleUserKey>>(list, HttpRestStatus.OK);
	}
	
	// 新增用户应用
	@PreAuthorize("hasRole('R_MANAG_U_E')")
	@ApiOperation(value = "Add SysRoleUser", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createSysRoleUser(@RequestBody SysRoleUserKey sysRoleUser,
			UriComponentsBuilder ucBuilder) {

		sysRoleUser.setRoleId(System.currentTimeMillis() + "");
		sysRoleUserService.insert(sysRoleUser);

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/sysRoleUser/{id}").buildAndExpand(sysRoleUser.getRoleId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,localeMessageSourceService.getMessage("common.create.created.message"));
	}

	// 修改用户应用信息
	@PreAuthorize("hasRole('R_MANAG_U_E')")
	@ApiOperation(value = "Edit SysRoleUser", notes = "")
	@RequestMapping(value = "/{roleId}/{userId}", method = RequestMethod.PUT)
	public ResponseRestEntity<SysRoleUserKey> updateApp(@PathVariable("roleId") String roleId,
			@PathVariable("userId") String userId, @RequestBody SysRoleUserKey sysRoleUser) {
		SysRoleUserKey sysRoleUserKey = new SysRoleUserKey();
		sysRoleUserKey.setRoleId(roleId);
		sysRoleUserKey.setUserId(userId);
		SysRoleUserKey currentSysRoleUser = sysRoleUserService.selectByPrimaryKey(sysRoleUserKey);

		if (currentSysRoleUser == null) {
			return new ResponseRestEntity<SysRoleUserKey>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}

		currentSysRoleUser.setRoleId(sysRoleUser.getRoleId());
		currentSysRoleUser.setUserId(sysRoleUser.getUserId());
		sysRoleUserService.updateByPrimaryKey(currentSysRoleUser);

		return new ResponseRestEntity<SysRoleUserKey>(currentSysRoleUser, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	// 修改部分用户应用信息
	@PreAuthorize("hasRole('R_MANAG_U_E')")
	@ApiOperation(value = "Edit  part SysRoleUser", notes = "")
	@RequestMapping(value = "/{roleId}/{userId}", method = RequestMethod.PATCH)
	public ResponseRestEntity<SysRoleUserKey> updateAppSelective(@PathVariable("roleId") String roleId,
			@PathVariable("userId") String userId, @RequestBody SysRoleUserKey sysRoleUser) {
		SysRoleUserKey sysRoleUserKey = new SysRoleUserKey();
		sysRoleUserKey.setRoleId(roleId);
		sysRoleUserKey.setUserId(userId);
		SysRoleUserKey currentSysRoleUser = sysRoleUserService.selectByPrimaryKey(sysRoleUserKey);

		if (currentSysRoleUser == null) {
			return new ResponseRestEntity<SysRoleUserKey>(HttpRestStatus.NOT_FOUND);
		}
		sysRoleUser.setRoleId(roleId);
		sysRoleUser.setUserId(userId);
		sysRoleUserService.updateByPrimaryKeySelective(sysRoleUser);

		return new ResponseRestEntity<SysRoleUserKey>(currentSysRoleUser, HttpRestStatus.OK);
	}

	// 删除指定用户应用
	@PreAuthorize("hasRole('R_MANAG_U_E')")
	@ApiOperation(value = "Delete SysRoleUser", notes = "")
	@RequestMapping(value = "/{roleId}/{userId}", method = RequestMethod.DELETE)
	public ResponseRestEntity<SysRoleUserKey> deleteSysRoleUser(@PathVariable("roleId") String roleId,
			@PathVariable("userId") String userId) {

		SysRoleUserKey sysRoleUserKey = new SysRoleUserKey();
		sysRoleUserKey.setRoleId(roleId);
		sysRoleUserKey.setUserId(userId);
		SysRoleUserKey sysRoleUser = sysRoleUserService.selectByPrimaryKey(sysRoleUserKey);
		if (sysRoleUser == null) {
			return new ResponseRestEntity<SysRoleUserKey>(HttpRestStatus.NOT_FOUND);
		}

		sysRoleUserService.deleteByPrimaryKey(sysRoleUserKey);
		return new ResponseRestEntity<SysRoleUserKey>(HttpRestStatus.NO_CONTENT);
	}

}
