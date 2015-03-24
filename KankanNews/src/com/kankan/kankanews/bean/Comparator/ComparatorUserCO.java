package com.kankan.kankanews.bean.Comparator;

import java.util.Comparator;

import com.kankan.kankanews.bean.User_Collect_Offline;

public class ComparatorUserCO implements Comparator {

	@Override
	public int compare(Object lhs, Object rhs) {
		User_Collect_Offline lu = (User_Collect_Offline) lhs;
		User_Collect_Offline ru = (User_Collect_Offline) rhs;

		return (int) (ru.getOfflineTime() - lu.getOfflineTime());

	}

}
