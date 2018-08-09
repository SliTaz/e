package com.zbensoft.e.payment.api.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zbensoft.e.payment.api.common.CommonFun;
import com.zbensoft.e.payment.api.common.MessageDef;
import com.zbensoft.e.payment.api.service.api.SysMenuService;
import com.zbensoft.e.payment.db.domain.SysMenu;
import com.zbensoft.e.payment.db.domain.SysMenuUserMenuParam;
import com.zbensoft.e.payment.db.domain.SysMenuUserMenuResponse;
import com.zbensoft.e.payment.db.mapper.SysMenuMapper;

@Service
public class SysMenuServiceImpl implements SysMenuService {

	@Autowired
	SysMenuMapper sysMenuMapper;

	@Override
	public int deleteByPrimaryKey(String menuId) {
		return sysMenuMapper.deleteByPrimaryKey(menuId);
	}

	@Override
	public int insert(SysMenu record) {
		return sysMenuMapper.insert(record);
	}

	@Override
	public int count(SysMenu menu) {
		return sysMenuMapper.count(menu);
	}

	@Override
	public int insertSelective(SysMenu record) {
		return sysMenuMapper.insertSelective(record);
	}

	@Override
	public SysMenu selectByPrimaryKey(String menuId) {
		return sysMenuMapper.selectByPrimaryKey(menuId);
	}

	@Override
	public int updateByPrimaryKeySelective(SysMenu record) {
		return sysMenuMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(SysMenu record) {
		return sysMenuMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<SysMenu> selectPage(SysMenu record) {
		return sysMenuMapper.selectPage(record);
	}

	@Override
	public void deleteAll() {
		sysMenuMapper.deleteAll();
	}

	@Override
	public List<SysMenuUserMenuResponse> getUserMenus(SysMenuUserMenuParam sysMenuUserMenuParam, String weburl) {
		if (weburl != null && weburl.length() > 0) {
			int indexhtml = weburl.indexOf(".html");
			if (indexhtml > 0) {
				weburl = weburl.substring(1, indexhtml + 5);
			} else {
				weburl = weburl.substring(1);
			}
		}
		List<SysMenuUserMenuResponse> sysMenuUserMenuResponseList = new ArrayList<>();
		List<SysMenu> sysMenuList = sysMenuMapper.getUserMenus(sysMenuUserMenuParam);
		if (sysMenuList != null && sysMenuList.size() > 0) {
			for (SysMenu sysMenu : sysMenuList) {
				if (sysMenuUserMenuParam.getMenuType() == MessageDef.MENU_TYPE.FUNCTION) {
					sysMenuUserMenuResponseList.add(new SysMenuUserMenuResponse(sysMenu));
				} else {
					if ("-1".equals(sysMenu.getPreMenuId())) {
						SysMenuUserMenuResponse sysMenuUserMenuResponse2 = new SysMenuUserMenuResponse(sysMenu);
						sysMenuUserMenuResponseList.add(sysMenuUserMenuResponse2);
						boolean retBoolean = addNodes(sysMenuUserMenuResponse2, sysMenuList, weburl);
						if (retBoolean) {
							sysMenuUserMenuResponse2.setActiveClass("active");
						}
					}
				}
			}
		}
		return sysMenuUserMenuResponseList;
	}

	private boolean addNodes(SysMenuUserMenuResponse sysMenuUserMenuResponse, List<SysMenu> sysMenuList, String weburl) {
		boolean isActive = false;
		for (SysMenu sysMenu : sysMenuList) {
			if (sysMenuUserMenuResponse.getMenuId().equals(sysMenu.getPreMenuId())) {
				SysMenuUserMenuResponse sysMenuUserMenuResponse2 = new SysMenuUserMenuResponse(sysMenu);
				sysMenuUserMenuResponse.getNodes().add(sysMenuUserMenuResponse2);
				boolean retBoolean = addNodes(sysMenuUserMenuResponse2, sysMenuList, weburl);
				if (retBoolean) {
					isActive = retBoolean;
					sysMenuUserMenuResponse.setActiveClass("active");
				}
				if (!isActive) {
					if (sysMenuUserMenuResponse2.getMenuProces() != null && weburl != null && weburl.length() > 0 && sysMenuUserMenuResponse2.getMenuProces().contains(weburl)) {
						sysMenuUserMenuResponse2.setActiveClass("active");
						isActive = true;
					}
				}
			}
		}

		return isActive;
	}

	@Override
	public boolean isRoleExist(SysMenu menu) {
		// TODO Auto-generated method stub
		return selectByMenuName(menu.getMenuNameCn()) != null;
	}

	@Override
	public SysMenu selectByMenuName(String name) {
		// TODO Auto-generated method stub
		return sysMenuMapper.selectByMenuName(name);
	}

	@Override
	public List<SysMenu> getRoleResources(String id) {

		return sysMenuMapper.getRoleResources(id);
	}

	@Override
	public List<SysMenu> findAll() {

		return sysMenuMapper.findAll();
	}

	@Override
	@Transactional(value = "DataSourceManager")
	public void saveRoleRescours(String roleId, List<String> list) {
		// 先删除该角色的所有资源
		sysMenuMapper.deleteRoleRescours(roleId);

		// 在为该角色添加资源
		for (String rId : list) {
			if (!CommonFun.isEmpty(rId)) {
				SysMenu resources = new SysMenu();
				resources.setRescId(rId);
				resources.setRoleId(roleId);
				sysMenuMapper.saveRoleRescours(resources);
			}
		}
	}

	//根据菜单关键字获得根菜单名称
	@Override
	public SysMenu findTopMenu(String keyWord) {
		SysMenu sysMenu = sysMenuMapper.getMenuByMenuKeyWord(keyWord);
		SysMenu rootMenu = getRootMenu(sysMenu);
		return rootMenu;
	}
	

	private SysMenu getRootMenu(SysMenu sysMenu) {
		SysMenu parentMenu = sysMenuMapper.selectByPrimaryKey(sysMenu.getPreMenuId());
		if ("-1".equals(parentMenu.getPreMenuId())) {
			return parentMenu;
		} else {
			return getRootMenu(parentMenu);
		}
	}

}
