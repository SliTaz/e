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
import com.zbensoft.e.payment.api.service.api.GovRoleUserService;
import com.zbensoft.e.payment.db.domain.GovRoleUserKey;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/govRoleUser")
@RestController
public class GovRoleUserController {
	@Autowired
	GovRoleUserService govRoleUserService;
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	// 查询用户应用，支持分页
	@PreAuthorize("hasRole('R_GOV_U_Q')")
	@ApiOperation(value = "Query GovRoleUser，Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<GovRoleUserKey>> selectPage(@RequestParam(required = false) String id,
			@RequestParam(required = false) String userId, 
			@RequestParam(required = false) String start, @RequestParam(required = false) String length) {
		GovRoleUserKey govRoleUserKey = new GovRoleUserKey();
		govRoleUserKey.setRoleId(id);
		govRoleUserKey.setUserId(userId);
		
		int count = govRoleUserService.count(govRoleUserKey);
		List<GovRoleUserKey> list = govRoleUserService.selectPage(govRoleUserKey);
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<GovRoleUserKey>>(new ArrayList<GovRoleUserKey>(), HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<List<GovRoleUserKey>>(list, HttpRestStatus.OK, count, count);
	}

	// 查询用户应用
	@PreAuthorize("hasRole('R_GOV_U_Q')")
	@ApiOperation(value = "Query GovRoleUser ", notes = "")
	@RequestMapping(value = "/{roleId}/{userId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<GovRoleUserKey> selectByPrimaryKey(@PathVariable("roleId") String roleId,
			@PathVariable("userId") String userId) {

		GovRoleUserKey govRoleUserKey = new GovRoleUserKey();
		govRoleUserKey.setRoleId(roleId);
		govRoleUserKey.setUserId(userId);
		GovRoleUserKey govRoleUser = govRoleUserService.selectByPrimaryKey(govRoleUserKey);
		if (govRoleUser == null) {
			return new ResponseRestEntity<GovRoleUserKey>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<GovRoleUserKey>(govRoleUser, HttpRestStatus.OK);
	}


	@PreAuthorize("hasRole('R_GOV_U_E')")
	@ApiOperation(value = "Add GovRoleUser", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createGovRoleUser(@RequestBody GovRoleUserKey govRoleUser,
			UriComponentsBuilder ucBuilder) {

		govRoleUser.setRoleId(System.currentTimeMillis() + "");
		govRoleUserService.insert(govRoleUser);

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/govRoleUser/{id}").buildAndExpand(govRoleUser.getRoleId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,localeMessageSourceService.getMessage("common.create.created.message"));
	}


	@PreAuthorize("hasRole('R_GOV_U_E')")
	@ApiOperation(value = "Edit GovRoleUser", notes = "")
	@RequestMapping(value = "/{roleId}/{userId}", method = RequestMethod.PUT)
	public ResponseRestEntity<GovRoleUserKey> updateApp(@PathVariable("roleId") String roleId,
			@PathVariable("userId") String userId, @RequestBody GovRoleUserKey govRoleUser) {
		GovRoleUserKey govRoleUserKey = new GovRoleUserKey();
		govRoleUserKey.setRoleId(roleId);
		govRoleUserKey.setUserId(userId);
		GovRoleUserKey currentGovRoleUser = govRoleUserService.selectByPrimaryKey(govRoleUserKey);

		if (currentGovRoleUser == null) {
			return new ResponseRestEntity<GovRoleUserKey>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}

		currentGovRoleUser.setRoleId(govRoleUser.getRoleId());
		currentGovRoleUser.setUserId(govRoleUser.getUserId());
		govRoleUserService.updateByPrimaryKey(currentGovRoleUser);

		return new ResponseRestEntity<GovRoleUserKey>(currentGovRoleUser, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}


	@PreAuthorize("hasRole('R_GOV_U_E')")
	@ApiOperation(value = "Edit part GovRoleUser", notes = "")
	@RequestMapping(value = "/{roleId}/{userId}", method = RequestMethod.PATCH)
	public ResponseRestEntity<GovRoleUserKey> updateAppSelective(@PathVariable("roleId") String roleId,
			@PathVariable("userId") String userId, @RequestBody GovRoleUserKey govRoleUser) {
		GovRoleUserKey govRoleUserKey = new GovRoleUserKey();
		govRoleUserKey.setRoleId(roleId);
		govRoleUserKey.setUserId(userId);
		GovRoleUserKey currentGovRoleUser = govRoleUserService.selectByPrimaryKey(govRoleUserKey);

		if (currentGovRoleUser == null) {
			return new ResponseRestEntity<GovRoleUserKey>(HttpRestStatus.NOT_FOUND);
		}
		govRoleUser.setRoleId(roleId);
		govRoleUser.setUserId(userId);
		govRoleUserService.updateByPrimaryKeySelective(govRoleUser);

		return new ResponseRestEntity<GovRoleUserKey>(currentGovRoleUser, HttpRestStatus.OK);
	}

	// 删除指定角色菜单
	@PreAuthorize("hasRole('R_GOV_U_E')")
	@ApiOperation(value = "Delete GovRoleUser", notes = "")
	@RequestMapping(value = "/{roleId}/{userId}", method = RequestMethod.DELETE)
	public ResponseRestEntity<GovRoleUserKey> deleteGovRoleUser(@PathVariable("roleId") String roleId,
			@PathVariable("userId") String userId) {

		GovRoleUserKey govRoleUserKey = new GovRoleUserKey();
		govRoleUserKey.setRoleId(roleId);
		govRoleUserKey.setUserId(userId);
		GovRoleUserKey govRoleUser = govRoleUserService.selectByPrimaryKey(govRoleUserKey);
		if (govRoleUser == null) {
			return new ResponseRestEntity<GovRoleUserKey>(HttpRestStatus.NOT_FOUND);
		}

		govRoleUserService.deleteByPrimaryKey(govRoleUserKey);
		return new ResponseRestEntity<GovRoleUserKey>(HttpRestStatus.NO_CONTENT);
	}
	//新的查询
	@PreAuthorize("hasRole('R_GOV_U_Q')")
	@ApiOperation(value = "Query GovRoleUserKey", notes = "")
	@RequestMapping(value = "/{userId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<List<GovRoleUserKey>> selectByUserId(@PathVariable("userId") String userId) {
		List<GovRoleUserKey> list = govRoleUserService.selectByUserId(userId);
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<GovRoleUserKey>>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<List<GovRoleUserKey>>(list, HttpRestStatus.OK);
	}

}
