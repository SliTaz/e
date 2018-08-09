package com.zbensoft.e.payment.api.control;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import com.zbensoft.e.payment.api.service.api.MerchantRoleUserService;
import com.zbensoft.e.payment.db.domain.MerchantRoleUserKey;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/merchantRoleUser")
@RestController
public class MerchantRoleUserController {
	@Autowired
	MerchantRoleUserService merchantRoleUserService;
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	// 查询用户应用，支持分页
	@ApiOperation(value = "Query MerchantRoleUser,Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<MerchantRoleUserKey>> selectPage(@RequestParam(required = false) String id,
			@RequestParam(required = false) String userId, 
			@RequestParam(required = false) String start, @RequestParam(required = false) String length) {
		MerchantRoleUserKey merchantRoleUserKey = new MerchantRoleUserKey();
		merchantRoleUserKey.setRoleId(id);
		merchantRoleUserKey.setUserId(userId);
		
/*		if (start != null && length != null) {
			merchantRoleUserKey.setPageStart(Integer.parseInt(start));
			merchantRoleUserKey.setPageEnd(Integer.parseInt(start) + Integer.parseInt(length));
		} else {
			merchantRoleUserKey.setPageStart(null);
			merchantRoleUserKey.setPageEnd(null);
		}*/
		int count = merchantRoleUserService.count(merchantRoleUserKey);
		List<MerchantRoleUserKey> list = merchantRoleUserService.selectPage(merchantRoleUserKey);
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<MerchantRoleUserKey>>(new ArrayList<MerchantRoleUserKey>(), HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<List<MerchantRoleUserKey>>(list, HttpRestStatus.OK, count, count);
	}

	// 查询用户应用
	@ApiOperation(value = "Query MerchantRoleUser", notes = "")
	@RequestMapping(value = "/{roleId}/{userId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<MerchantRoleUserKey> selectByPrimaryKey(@PathVariable("roleId") String roleId,
			@PathVariable("userId") String userId) {

		MerchantRoleUserKey merchantRoleUserKey = new MerchantRoleUserKey();
		merchantRoleUserKey.setRoleId(roleId);
		merchantRoleUserKey.setUserId(userId);
		MerchantRoleUserKey merchantRoleUser = merchantRoleUserService.selectByPrimaryKey(merchantRoleUserKey);
		if (merchantRoleUser == null) {
			return new ResponseRestEntity<MerchantRoleUserKey>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<MerchantRoleUserKey>(merchantRoleUser, HttpRestStatus.OK);
	}
	
	
	@ApiOperation(value = "Query MerchantRoleUser", notes = "")
	@RequestMapping(value = "/{userId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<List<MerchantRoleUserKey>> selectByPayAppId(@PathVariable("userId") String userId) {
		List<MerchantRoleUserKey> list = merchantRoleUserService.selectByUserId(userId);
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<MerchantRoleUserKey>>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<List<MerchantRoleUserKey>>(list, HttpRestStatus.OK);
	}

	// 新增用户应用
	@ApiOperation(value = "Add MerchantRoleUser", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createMerchantRoleUser(@RequestBody MerchantRoleUserKey merchantRoleUser,
			UriComponentsBuilder ucBuilder) {

		merchantRoleUser.setRoleId(System.currentTimeMillis() + "");
		merchantRoleUserService.insert(merchantRoleUser);

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/merchantRoleUser/{id}").buildAndExpand(merchantRoleUser.getRoleId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,localeMessageSourceService.getMessage("common.create.created.message"));
	}

	// 修改用户应用信息
	@ApiOperation(value = "Edit MerchantRoleUser", notes = "")
	@RequestMapping(value = "/{roleId}/{userId}", method = RequestMethod.PUT)
	public ResponseRestEntity<MerchantRoleUserKey> updateApp(@PathVariable("roleId") String roleId,
			@PathVariable("userId") String userId, @RequestBody MerchantRoleUserKey merchantRoleUser) {
		MerchantRoleUserKey merchantRoleUserKey = new MerchantRoleUserKey();
		merchantRoleUserKey.setRoleId(roleId);
		merchantRoleUserKey.setUserId(userId);
		MerchantRoleUserKey currentMerchantRoleUser = merchantRoleUserService.selectByPrimaryKey(merchantRoleUserKey);

		if (currentMerchantRoleUser == null) {
			return new ResponseRestEntity<MerchantRoleUserKey>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}

		currentMerchantRoleUser.setRoleId(merchantRoleUser.getRoleId());
		currentMerchantRoleUser.setUserId(merchantRoleUser.getUserId());
		merchantRoleUserService.updateByPrimaryKey(currentMerchantRoleUser);

		return new ResponseRestEntity<MerchantRoleUserKey>(currentMerchantRoleUser, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	// 修改部分用户应用信息
	@ApiOperation(value = "Edit part MerchantRoleUser", notes = "")
	@RequestMapping(value = "/{roleId}/{userId}", method = RequestMethod.PATCH)
	public ResponseRestEntity<MerchantRoleUserKey> updateAppSelective(@PathVariable("roleId") String roleId,
			@PathVariable("userId") String userId, @RequestBody MerchantRoleUserKey merchantRoleUser) {
		MerchantRoleUserKey merchantRoleUserKey = new MerchantRoleUserKey();
		merchantRoleUserKey.setRoleId(roleId);
		merchantRoleUserKey.setUserId(userId);
		MerchantRoleUserKey currentMerchantRoleUser = merchantRoleUserService.selectByPrimaryKey(merchantRoleUserKey);

		if (currentMerchantRoleUser == null) {
			return new ResponseRestEntity<MerchantRoleUserKey>(HttpRestStatus.NOT_FOUND);
		}
		merchantRoleUser.setRoleId(roleId);
		merchantRoleUser.setUserId(userId);
		merchantRoleUserService.updateByPrimaryKeySelective(merchantRoleUser);

		return new ResponseRestEntity<MerchantRoleUserKey>(currentMerchantRoleUser, HttpRestStatus.OK);
	}

	// 删除指定用户应用
	@ApiOperation(value = "Delete MerchantRoleUser", notes = "")
	@RequestMapping(value = "/{roleId}/{userId}", method = RequestMethod.DELETE)
	public ResponseRestEntity<MerchantRoleUserKey> deleteMerchantRoleUser(@PathVariable("roleId") String roleId,
			@PathVariable("userId") String userId) {

		MerchantRoleUserKey merchantRoleUserKey = new MerchantRoleUserKey();
		merchantRoleUserKey.setRoleId(roleId);
		merchantRoleUserKey.setUserId(userId);
		MerchantRoleUserKey merchantRoleUser = merchantRoleUserService.selectByPrimaryKey(merchantRoleUserKey);
		if (merchantRoleUser == null) {
			return new ResponseRestEntity<MerchantRoleUserKey>(HttpRestStatus.NOT_FOUND);
		}

		merchantRoleUserService.deleteByPrimaryKey(merchantRoleUserKey);
		return new ResponseRestEntity<MerchantRoleUserKey>(HttpRestStatus.NO_CONTENT);
	}

}
