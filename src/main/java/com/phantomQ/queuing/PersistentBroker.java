package com.phantomQ.queuing;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class PersistentBroker  {

	@PersistenceContext(unitName="entityManager")
	private EntityManager entityManager;

	

}
