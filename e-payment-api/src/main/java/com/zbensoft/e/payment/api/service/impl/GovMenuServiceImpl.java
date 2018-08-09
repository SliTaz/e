package com.zbensoft.e.payment.api.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zbensoft.e.payment.api.common.CommonFun;
import com.zbensoft.e.payment.api.common.MessageDef;
import com.zbensoft.e.payment.api.common.PageHelperUtil;
import com.zbensoft.e.payment.api.service.api.GovMenuService;
import com.zbensoft.e.payment.db.domain.GovMenu;
import com.zbensoft.e.payment.db.domain.GovMenuUserMenuResponse;
import com.zbensoft.e.payment.db.domain.SysMenuUserMenuParam;
import com.zbensoft.e.payment.db.mapper.GovMenuMapper;

@Service
public class GovMenuServiceImpl implements GovMenuService {
	@Autowired
	GovMenuMapper govMenuMapper;

	@Override
	public int deleteByPrimaryKey(String menuId) {

		return govMenuMapper.deleteByPrimaryKey(menuId);
	}

	@Override
	public int insert(GovMenu record) {

		return govMenuMapper.insert(record);
	}

	@Override
	public int insertSelective(GovMenu record) {

		return govMenuMapper.insertSelective(record);
	}

	@Override
	public GovMenu selectByPrimaryKey(String menuId) {

		return govMenuMapper.selectByPrimaryKey(menuId);
	}

	@Override
	public int updateByPrimaryKeySelective(GovMenu record) {

		return govMenuMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(GovMenu record) {

		return govMenuMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<GovMenu> selectPage(GovMenu record) {
		return govMenuMapper.selectPage(record);
	}

	@Override
	public void deleteAll() {
		govMenuMapper.deleteAll();
	}

	@Override
	public int count(GovMenu govMenu) {
		return govMenuMapper.count(govMenu);
	}

	@Override
	public boolean isGovExist(GovMenu govMenu) {
		return selectByRovName(govMenu.getMenuNameEn()) != null;
	}

	@Override
	public GovMenu selectByRovName(String name) {
		return govMenuMapper.selectByRovName(name);
	}

	@Override
	public List<GovMenu> getRoleResources(String id) {
		return govMenuMapper.getRoleResources(id);
	}

	@Override
	@Transactional(value = "DataSourceManager")
	public void saveRoleRescours(String roleId, List<String> list) {
		// 先删除该角色的所有资源
		govMenuMapper.deleteRoleRescours(roleId);

		// 在为该角色添加资源
		for (String rId : list) {
			if (!CommonFun.isEmpty(rId)) {
				GovMenu resources = new GovMenu();
				resources.setRescId(rId);
				resources.setRoleId(roleId);
				govMenuMapper.saveRoleRescours(resources);
			}
		}
	}

	@Override
	public List<GovMenuUserMenuResponse> getUserMenus(SysMenuUserMenuParam sysMenuUserMenuParam, String weburl) {
		if (weburl != null && weburl.length() > 0) {
			int indexhtml = weburl.indexOf(".html");
			if (indexhtml > 0) {
				weburl = weburl.substring(1, indexhtml + 5);
			} else {
				weburl = weburl.substring(1);
			}
		}
		List<GovMenuUserMenuResponse> govMenuUserMenuResponseslist = new ArrayList<>();
		List<GovMenu> govMenuList = govMenuMapper.getUserMenus(sysMenuUserMenuParam);
		if (govMenuList != null && govMenuList.size() > 0) {
			for (GovMenu govMenu : govMenuList) {
				if (sysMenuUserMenuParam.getMenuType() == MessageDef.MENU_TYPE.FUNCTION) {
					govMenuUserMenuResponseslist.add(new GovMenuUserMenuResponse(govMenu));
				} else {
					if ("-1".equals(govMenu.getPreMenuId())) {
						GovMenuUserMenuResponse govMenuUserMenuResponses2 = new GovMenuUserMenuResponse(govMenu);
						govMenuUserMenuResponseslist.add(govMenuUserMenuResponses2);
						boolean retBoolean = addNodes(govMenuUserMenuResponses2, govMenuList, weburl);
						if (retBoolean) {
							govMenuUserMenuResponses2.setActiveClass("active");
						}
					}
				}
			}
		}
		return govMenuUserMenuResponseslist;
	}

	private boolean addNodes(GovMenuUserMenuResponse govMenuUserMenuResponse, List<GovMenu> govMenuList, String weburl) {
		boolean isActive = false;
		for (GovMenu govMenu : govMenuList) {
			if (govMenuUserMenuResponse.getMenuId().equals(govMenu.getPreMenuId())) {
				GovMenuUserMenuResponse govMenuUserMenuResponse2 = new GovMenuUserMenuResponse(govMenu);
				govMenuUserMenuResponse.getNodes().add(govMenuUserMenuResponse2);
				boolean retBoolean = addNodes(govMenuUserMenuResponse2, govMenuList, weburl);
				if (retBoolean) {
					isActive = retBoolean;
					govMenuUserMenuResponse.setActiveClass("active");
				}
				if (!isActive) {
					if (govMenuUserMenuResponse2.getMenuProces() != null && weburl != null && weburl.length() > 0 && govMenuUserMenuResponse2.getMenuProces().contains(weburl)) {
						govMenuUserMenuResponse2.setActiveClass("active");
						isActive = true;
					}
				}
			}
		}

		return isActive;
	}

	@Override
	public GovMenu selectByMenuName(String name) {
		return govMenuMapper.selectByRovName(name);
	}

	@Override
	public List<GovMenu> findAll() {
		// TODO Auto-generated method stub
		return govMenuMapper.findAll();
	}
}
