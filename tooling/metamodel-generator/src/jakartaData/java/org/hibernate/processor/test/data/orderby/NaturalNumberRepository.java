/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.processor.test.data.orderby;

import jakarta.data.Limit;
import jakarta.data.Order;
import jakarta.data.Sort;
import jakarta.data.page.CursoredPage;
import jakarta.data.page.Page;
import jakarta.data.page.PageRequest;
import jakarta.data.repository.By;
import jakarta.data.repository.DataRepository;
import jakarta.data.repository.Find;
import jakarta.data.repository.OrderBy;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;

import java.util.List;


@Repository
public interface NaturalNumberRepository extends DataRepository<NaturalNumber, Long> {

	@OrderBy("id")
	@Query("SELECT id from NaturalNumber WHERE isOdd = true AND id BETWEEN 21 AND ?1")
	Page<Long> oddsFrom21To(long max, PageRequest pageRequest);

	@OrderBy("id")
	@Query("SELECT id from NaturalNumber WHERE isOdd = true AND id BETWEEN 21 AND ?1")
	Page<Long> oddsFrom21ToOrdered(long max, PageRequest pageRequest, Order<NaturalNumber> order);

	@OrderBy("id")
	@Find
	List<NaturalNumber> findByIsOdd(@By("isOdd") boolean isOdd);

	@Find
	List<NaturalNumber> findByIsOddSorted(@By("isOdd") boolean isOdd, Order<NaturalNumber> order);

	@Query("from NaturalNumber")
	CursoredPage<NaturalNumber> everyNumber(PageRequest pageRequest, Order<NaturalNumber> order);

	@OrderBy("id")
	@Query("SELECT id from NaturalNumber WHERE isOdd = true AND id BETWEEN 21 AND ?1")
	List<Long> findAllWithSort(long max, Limit limit, Sort<NaturalNumber>... order);
}
