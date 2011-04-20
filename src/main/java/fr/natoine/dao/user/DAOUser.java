/*
 * Copyright 2010 Antoine Seilles (Natoine)
 *   This file is part of dao-user.

    controler-user is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    controler-user is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with controler-user.  If not, see <http://www.gnu.org/licenses/>.

 */

package fr.natoine.dao.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import fr.natoine.model_resource.Resource;
import fr.natoine.model_user.Agent;
import fr.natoine.model_user.AgentStatus;
import fr.natoine.model_user.Application;
import fr.natoine.model_user.Person;
import fr.natoine.model_user.UserAccount;
import fr.natoine.stringOp.StringOp;

public class DAOUser 
{
	private EntityManagerFactory emf = null ;
	
	public DAOUser(EntityManagerFactory _emf)
	{
		emf = _emf ;
	}
	
	/*CreateApplication*/
	/**
	 * Creates an Application. Return false if unable to store it.
	 * Beware : Application's label is unique in the database.
	 * @param _label Label of the Application
	 * @param _description Description od the Application
	 * @param _represents Resource to represent the Application
	 */
	public boolean createApplication(String _label, String _description, Resource _represents)
	{
		_label = StringOp.deleteBlanks(_label);
		if(!StringOp.isNull(_label))
		{
			if(_description != null) _description = StringOp.deleteBlanks(_description);
			Application app = new Application();
			app.setDescription(_description);
			app.setInscription(new Date());
			app.setLabel(_label);
			app.setRepresents(_represents);
			EntityManager em = emf.createEntityManager();
	        EntityTransaction tx = em.getTransaction();
	        try{
		        tx.begin();
		        if(_represents.getId() != null)
	        	{
		        	Resource synchro_resource = em.find(_represents.getClass(), _represents.getId());
					if(synchro_resource != null) app.setRepresents(synchro_resource);
	        	}
		        em.persist(app);
		        tx.commit();
		        return true ;
	        }
	        catch(Exception e)
	        {
	        	tx.rollback();
	        	System.out.println("[DAOUser.createApplication] fails to create application"
	        			+ " label : " + _label 
	        			+ " description : " + _description
	        			+ " cause : " + e.getMessage());
	        	return false;
	        }
		}
		else
		{
			System.out.println("[DAOUser.createApplication] fails to create application"
        			+ " label : " + _label 
        			+ " cause : this value is not correct" );
		return false ;
		}		
	}
	
	/*RetrieveApplication*/
	/**
	 * Retrieves an Application with the specified label.
	 * @param _label Label of the Application
	 * @return an Application, a new Application with values setted to null if no one exist with the specified label
	 */
	public Application retrieveApplication(String _label)
	{
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		try{
			tx.begin();
			Application app = (Application) em.createQuery("from Application where label = ?").setParameter(1, _label).getSingleResult();
			tx.commit();
			return app;
		}
		catch(Exception e)
		{
			tx.rollback();
			System.out.println("[DAOUser.retrieveApplication] unable to retrieve Application"
					+ " label : " + _label
					+ " cause : " + e.getMessage());
			return new Application();
		}
	}
	/**
	 * Retrieves an Application with the specified id
	 * @param _id
	 * @return the Application or a new Application with all values setted to null.
	 */
	public Application retrieveApplication(long _id)
	{
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		try{
			tx.begin();
			Application app = (Application) em.createQuery("from Application where id = ?").setParameter(1, _id).getSingleResult();
			tx.commit();
			return app;
		}
		catch(Exception e)
		{
			tx.rollback();
			System.out.println("[DAOUser.retrieveApplication] unable to retrieve Application"
					+ " id : " + _id
					+ " cause : " + e.getMessage());
			return new Application();
		}
	}
	
	/*CreateAgentStatus*/
	/**
	 * Creates a AgentStatus
	 * AgentStatus are used to specify the status of an agent relatively to a resource.
	 * For example, an AgentStatus isAuthor can be used to tag a Resource and says that a Agent is author of the tagged resource.
	 * Beware : label for AgentStatus is unique in the database
	 * @param _label 
	 * @param _comment 
	 * @return
	 */
	public boolean createAgentStatus(String _label , String _comment)
	{
		_label = StringOp.deleteBlanks(_label);
		if(!StringOp.isNull(_label))
		{
			_comment = StringOp.deleteBlanks(_comment);
			AgentStatus as = new AgentStatus();
			as.setComment(_comment);
			as.setLabel(_label);	
			EntityManager em = emf.createEntityManager();
	        EntityTransaction tx = em.getTransaction();
	        try{
		        tx.begin();
		        em.persist(as);
		        tx.commit();
		        return true ;
	        }
	        catch(Exception e)
	        {
	        	tx.rollback();
	        	System.out.println("[DAOUser.createAgentStatus] unable to persist AgentStatus"
						+ " label : " + _label
						+ " comment : " + _comment
	        			+ " cause : " + e.getMessage());
	        	return false;
	        }
		}
		else
		{
			System.out.println("[DAOUser.createAgentStatus] unable to persist AgentStatus"
					+ " label : " + _label
					+ " comment : " + _comment
					+ "cause : label is invalid");
			return false;
		}
	}
	/**
	 * Creates an AgentStatus specifying an other AgentStatus
	 * AgentStatus are used to specify the status of an agent relatively to a resource.
	 * For example, an AgentStatus isAuthor can be used to tag a Resource and says that a Agent is author of the tagged resource.
	 * Beware : label for AgentStatus is unique in the database
	 * @param _label
	 * @param _comment
	 * @param _father
	 * @return
	 */
	public boolean createAgentStatusChild(String _label , String _comment, AgentStatus _father)
	{
		_label = StringOp.deleteBlanks(_label);
		if(!StringOp.isNull(_label))
		{
			_comment = StringOp.deleteBlanks(_comment);
			AgentStatus as = new AgentStatus();
			as.setComment(_comment);
			as.setLabel(_label);	
			as.setFather(_father);
			EntityManager em = emf.createEntityManager();
	        EntityTransaction tx = em.getTransaction();
	        try{
		        tx.begin();
		        if(_father.getId() != null)
				{
					AgentStatus synchro_father = em.find(_father.getClass(), _father.getId());
					if(synchro_father != null) as.setFather(synchro_father);
				}
		        em.persist(as);
		        tx.commit();
		        //em.close();
		        return true ;
	        }
	        catch(Exception e)
	        {
	        	tx.rollback();
	        	System.out.println("[DAOUser.createAgentStatus] unable to persist AgentStatus"
						+ " label : " + _label
						+ " comment : " + _comment
	        			+ " cause : " + e.getMessage());
	        	//em.close();
	        	return false;
	        }
		}
		else
		{
			System.out.println("[DAOUser.createAgentStatus] unable to persist AgentStatus"
					+ " label : " + _label
					+ " comment : " + _comment
					+ "cause : label is invalid");
			return false;
		}
	}
	/**
	 * Creates the default AgentStatus
	 * @return
	 */
	public boolean createAgentStatusDefault()
	{
		return this.createAgentStatus("default", "this is the default status for agent");
	}
	/**
	 * Add an AgentStatus to the collection of status of an Agent.
	 * The AgentStatus must exist in the database or it will be ignored.
	 * @param _agent
	 * @param _status
	 * @return
	 */
	public boolean addAgentStatusToAgent(Agent _agent, AgentStatus _status)
	{
		EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        if(_agent.getId() != null)
        {
        	_agent = em.find(_agent.getClass(), _agent.getId());
        	//System.out.println("[CreateAgentStatus.addAgentStatusToAgent] a retrouvé l'agent id : " + _agent.getId());
	        if(_status.getId() != null)
			{
	        	if(_agent.getStatus() == null) _agent.setStatus(new ArrayList<AgentStatus>());
				AgentStatus _synchro_status = em.find(AgentStatus.class, _status.getId());
				if(_synchro_status != null) _agent.getStatus().add(_synchro_status);
				//System.out.println("[CreateAgentStatus.addAgentStatusToAgent] a retrouvé le status d'id :" + _synchro_status.getId());
			}
	        em.merge(_agent);
        }
        else System.out.println("[DAOUSer.addAgentStatusToAgent] the agent doesn't exist in the database");
        tx.commit();
        return true ;
	}
	
	/*RetrieveAgentStatus*/
	/**
	 * Retrieves an AgentStatus with the specified label.
	 * @param _label Label of the AgentStatus
	 * @return the AgentStatus or a new AgentStatus with values setted to false
	 */
	public AgentStatus retrieveAgentStatus(String _label)
	{
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		try{
			tx.begin();
			AgentStatus _app = (AgentStatus) em.createQuery("from AgentStatus where label = ?").setParameter(1, _label).getSingleResult();
			tx.commit();
			return _app;
		}
		catch(Exception e)
		{
			tx.rollback();
			System.out.println("[DAOUser.retrieveAgentStatus] unable to retrieve AgentStatus"
					+ " label : " + _label
					+ " cause : " + e.getMessage());
			return new AgentStatus();
		}
	}
	/**
	 * Retrieves the Default AgentStatus
	 * @return
	 */
	public AgentStatus retrieveAgentStatusDefault()
	{
		return this.retrieveAgentStatus("default");
	}
	
	/*CreateAgent*/
	/**
	 * Creates an Agent
	 * @param _label
	 * @param _description
	 * @param _contextInscription
	 * @return
	 */
	public boolean createAgent(String _label, String _description, Application _contextInscription, Collection<AgentStatus> _status)
	{
		_label = StringOp.deleteBlanks(_label);
		if(!StringOp.isNull(_label))
		{
			_description = StringOp.deleteBlanks(_description);
			Agent agent = new Agent();
			agent.setDescription(_description);
			agent.setLabel(_label);
			agent.setContextInscription(_contextInscription);
			agent.setInscription(new Date());
			EntityManager em = emf.createEntityManager();
			EntityTransaction tx = em.getTransaction();
			try{
				tx.begin();
				if(_contextInscription.getId() != null)
				{
					Application synchro_context = em.find(_contextInscription.getClass(), _contextInscription.getId());
					if(synchro_context != null) agent.setContextInscription(synchro_context);
				}
				Collection<AgentStatus> synchro_added = new ArrayList<AgentStatus>();
				if(_status != null)
				{
					for(AgentStatus _agent_status : _status)
					{
						if(_agent_status.getId() != null)
						{
							AgentStatus synchro__agent_status = em.find(_agent_status.getClass(), _agent_status.getId());
							if(synchro__agent_status != null) synchro_added.add(synchro__agent_status);
						}
						else synchro_added.add(_agent_status);
					}
				}
				agent.setStatus(synchro_added);
				em.persist(agent);
				tx.commit();
				return true ;
			}
			catch(Exception e)
			{
				tx.rollback();
				e.printStackTrace();
				System.out.println("[DAOUser.createAgent] fails to create agent"
						+ " label : " + _label 
						+ " description : " + _description
						+ " cause : " + e.getMessage());
				return false;
			}
		}
		else
		{
			System.out.println("[DAOUser.createAgent] fails to create agent"
					+ " label : " + _label 
					+ " cause : this value is not correct" );
			return false ;
		}		
	}
	
	/*RetrieveAgent*/
	/**
	 * Retrieves an Agent with the specified id
	 * @param _id
	 * @return An agent, a new one with values setted to null if the specifier id is not in the database
	 */
	public Agent retrieveAgent(long _id)
	{
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		try{
			tx.begin();
			Agent agent = (Agent)em.createQuery("from Agent where id = ?").setParameter(1, _id).getSingleResult();
			tx.commit();
			return agent;
		}
		catch(Exception e)
		{
			tx.rollback();
			System.out.println("[DAOUser.retrieveAgent] unable to retrieve Agent"
					+ " id : " + _id
					+ " cause : " + e.getMessage());
			return new Agent();
		}
	}
	/**
	 * Retrieves all agents getting the specified label
	 * @param _label
	 * @return A list of Agent or an ArrayList empty
	 */
	public List<Agent> retrieveAgent(String _label)
	{
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		try{
			tx.begin();
			//Added a cast to List<Agent>
			List<Agent> agents = ((List<Agent>)em.createQuery("from Agent where label = ?").setParameter(1, _label).getResultList());
			tx.commit();
			return agents;
		}
		catch(Exception e)
		{
			tx.rollback();
			System.out.println("[DAOUser.retrieveAgent] unable to retrieve Agent"
					+ " label : " + _label
					+ " cause : " + e.getMessage());
			return new ArrayList<Agent>();
		}
	}
	
	/*CreatePerson*/
	/**
	 * Creates a Person.
	 * Beware : mail is unique in the database.
	 * @param _firstName
	 * @param _lastName
	 * @param _mail
	 * @param _description
	 * @param _represents
	 * @return
	 */
	public boolean createPerson(String _firstName, String _lastName, String _mail, String _description, Resource _represents, 
			Application _context_inscription, Collection<AgentStatus> _status)
	{
		_firstName = StringOp.deleteBlanks(_firstName);
		_lastName = StringOp.deleteBlanks(_lastName);
		if(StringOp.isValidMail(_mail) && !StringOp.isNull(_firstName) && !StringOp.isNull(_lastName))
		{
			Person person = new Person();
			person.setFirstName(_firstName);
			person.setLastName(_lastName);
			person.setMail(_mail);
			person.setDescription(_description);
			person.setLabel(_firstName + "_" + _lastName);
			person.setRepresents(_represents);
			person.setContextInscription(_context_inscription);
			person.setInscription(new Date());
			EntityManager em = emf.createEntityManager();
			EntityTransaction tx = em.getTransaction();
			try{
				tx.begin();
				if(_context_inscription.getId() != null)
				{
					Application synchro_context = em.find(Application.class, _context_inscription.getId());
					if(synchro_context != null) person.setContextInscription(synchro_context);
				}
				if(_represents.getId() != null)
				{
					Resource synchro_represents = em.find(Resource.class, _represents.getId());
					if(synchro_represents != null) person.setRepresents(synchro_represents);
				}
				Collection<AgentStatus> synchro_added = new ArrayList<AgentStatus>();
				if(_status != null)
				{
					for(AgentStatus _agent_status : _status)
					{
						if(_agent_status.getId() != null)
						{
							AgentStatus synchro__agent_status = em.find(_agent_status.getClass(), _agent_status.getId());
							if(synchro__agent_status != null) synchro_added.add(synchro__agent_status);
						}
						else synchro_added.add(_agent_status);
					}
				}
				person.setStatus(synchro_added);
				em.persist(person);
				tx.commit();
				return true ;
			}
			catch(Exception e)
			{
				//Le rollback était commenté ?
				tx.rollback();
				System.out.println("[DAOUser.createPerson] fails to create person"
						+ " firstName : " + _firstName 
						+ " lastName : " + _lastName
						+ " mail : " + _mail
						+ " description : " + _description
						+ " cause : " + e.getMessage());
				return false;
			}
		}
		else
		{
			System.out.println("[DAOUser.createPerson] fails to create person"
					+ " firstName : " + _firstName 
					+ " lastName : " + _lastName
					+ " mail : " + _mail
					+ " cause : one these values is not correct" );
			return false ;
		}
	}
	/**
	 * Creates a Person.
	 * Beware : mail is unique in the database.
	 * @param _firstName
	 * @param _lastName
	 * @param _mail
	 * @param _description
	 * @param _represents
	 * @return
	 */
	public Person createAndGetPerson(String _firstName, String _lastName, String _mail, String _description, Resource _represents, 
			Application _context_inscription, Collection<AgentStatus> _status)
	{
		_firstName = StringOp.deleteBlanks(_firstName);
		_lastName = StringOp.deleteBlanks(_lastName);
		if(StringOp.isValidMail(_mail) && !StringOp.isNull(_firstName) && !StringOp.isNull(_lastName))
		{
			Person person = new Person();
			person.setFirstName(_firstName);
			person.setLastName(_lastName);
			person.setMail(_mail);
			person.setDescription(_description);
			person.setLabel(_firstName + "_" + _lastName);
			person.setRepresents(_represents);
			person.setContextInscription(_context_inscription);
			person.setInscription(new Date());
			EntityManager em = emf.createEntityManager();
			EntityTransaction tx = em.getTransaction();
			try{
				tx.begin();
				if(_context_inscription.getId() != null)
				{
					Application synchro_context = em.find(Application.class, _context_inscription.getId());
					if(synchro_context != null) person.setContextInscription(synchro_context);
				}
				if(_represents.getId() != null)
				{
					Resource synchro_represents = em.find(Resource.class, _represents.getId());
					if(synchro_represents != null) person.setRepresents(synchro_represents);
				}
				Collection<AgentStatus> synchro_added = new ArrayList<AgentStatus>();
				if(_status != null)
				{
					for(AgentStatus _agent_status : _status)
					{
						if(_agent_status.getId() != null)
						{
							AgentStatus synchro__agent_status = em.find(_agent_status.getClass(), _agent_status.getId());
							if(synchro__agent_status != null) synchro_added.add(synchro__agent_status);
						}
						else synchro_added.add(_agent_status);
					}
				}
				person.setStatus(synchro_added);
				em.persist(person);
				tx.commit();
				return person ;
			}
			catch(Exception e)
			{
				//pareil, le rollback était commenté ???
				tx.rollback();
				System.out.println("[DAOUser.createPerson] fails to create person"
						+ " firstName : " + _firstName 
						+ " lastName : " + _lastName
						+ " mail : " + _mail
						+ " description : " + _description
						+ " cause : " + e.getMessage());
				return new Person();
			}
		}
		else
		{
			System.out.println("[DAOUser.createPerson] fails to create person"
					+ " firstName : " + _firstName 
					+ " lastName : " + _lastName
					+ " mail : " + _mail
					+ " cause : one these values is not correct" );
			return new Person() ;
		}
	}
	
	/*RetrievePerson*/
	/**
	 * Retrieves a Person getting the specified mail
	 * @param _mail
	 * @return
	 */
	public Person retrievePerson(String _mail)
	{
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		try{
			tx.begin();
			Person person = (Person) em.createQuery("from Person where mail = ?").setParameter(1, _mail).getSingleResult();
			tx.commit();
			return person;
		}
		catch(Exception e)
		{
			tx.rollback();
			System.out.println("[DAOUser.retrievePerson] unable to retrieve Person"
					+ " mail : " + _mail
					+ " cause : " + e.getMessage());
			return new Person();
		}
	}
	
	/*CreateUserAccount*/
	/**
	 * Creates a UserAccount
	 * The resource represents must exist
	 * @param _contextInscription
	 * @param _password
	 * @param _pseudonyme
	 * @param _description
	 * @param _represents
	 * @param _user
	 * @return
	 */
	public boolean createUserAccount(String _pseudonyme, String _password, String _description, Resource _represents, Person _user, 
			Application _contextInscription, Collection<AgentStatus> _status )
	{
		_pseudonyme = StringOp.deleteBlanks(_pseudonyme);
		if(!StringOp.isNull(_password) && !StringOp.isNull(_pseudonyme) && _represents.getId()!= null)
		{
			UserAccount userAccount = new UserAccount();
			userAccount.setContextInscription(_contextInscription);
			userAccount.setInscription(new Date());
			userAccount.setPassword(_password);
			userAccount.setPseudonyme(_pseudonyme);
			userAccount.setLabel(_pseudonyme);
			userAccount.setDescription(_description);
			userAccount.setRepresents(_represents);
			userAccount.setUser(_user);
			EntityManager em = emf.createEntityManager();
			EntityTransaction tx = em.getTransaction();
			try{
				tx.begin();
				if(_contextInscription.getId() != null)
				{
					Application synchro_context = em.find(_contextInscription.getClass(), _contextInscription.getId());
					if(synchro_context != null) userAccount.setContextInscription(synchro_context);
				}
				if(_represents.getId() != null)
				{
					Resource synchro_represents = em.find(_represents.getClass(), _represents.getId());
					if(synchro_represents != null) userAccount.setRepresents(synchro_represents);
				}
				if(_user.getId() != null)
				{
					Person synchro_user = em.find(_user.getClass(), _user.getId());
					if(synchro_user != null) userAccount.setUser(synchro_user);
				}
				Collection<AgentStatus> synchro_added = new ArrayList<AgentStatus>();
				if(_status != null)
				{
					for(AgentStatus _agent_status : _status)
					{
						if(_agent_status.getId() != null)
						{
							AgentStatus synchro__agent_status = em.find(_agent_status.getClass(), _agent_status.getId());
							if(synchro__agent_status != null) synchro_added.add(synchro__agent_status);
						}
						else synchro_added.add(_agent_status);
					}
				}
				userAccount.setStatus(synchro_added);
				em.persist(userAccount);
				tx.commit();
				return true ;
			}
			catch(Exception e)
			{
				//encore un rollback qui était commenté ???
				tx.rollback();
				System.out.println("[DAOUser.createUserAccount] fails to create UserAccount"
						+ " pseudonyme : " + _pseudonyme 
						+ " password : " + _password
						+ " cause : " + e.getMessage());
				return false;
			}
		}
		else
		{
			System.out.println("[DAOUser.createUserAccount] fails to create UserAccount"
					+ " pseudonyme : " + _pseudonyme 
					+ " password : " + _password
					+ " resource represents id : " + _represents.getId()
					+ " cause : one these values is not correct" );
			return false ;
		}
	}
	
	/**
	 * Creates and gets a UserAccount
	 * The resource represents must exist
	 * @param _contextInscription
	 * @param _password
	 * @param _pseudonyme
	 * @param _description
	 * @param _represents
	 * @param _user
	 * @return UserAccount
	 */
	public UserAccount createAndGetUserAccount(String _pseudonyme, String _password, String _description, Resource _represents, Person _user, 
			Application _contextInscription, Collection<AgentStatus> _status )
	{
		_pseudonyme = StringOp.deleteBlanks(_pseudonyme);
		if(!StringOp.isNull(_password) && !StringOp.isNull(_pseudonyme) && _represents.getId()!= null)
		{
			UserAccount userAccount = new UserAccount();
			userAccount.setContextInscription(_contextInscription);
			userAccount.setInscription(new Date());
			userAccount.setPassword(_password);
			userAccount.setPseudonyme(_pseudonyme);
			userAccount.setLabel(_pseudonyme);
			userAccount.setDescription(_description);
			userAccount.setRepresents(_represents);
			userAccount.setUser(_user);
			EntityManager em = emf.createEntityManager();
			EntityTransaction tx = em.getTransaction();
			try{
				tx.begin();
				if(_contextInscription.getId() != null)
				{
					Application synchro_context = em.find(_contextInscription.getClass(), _contextInscription.getId());
					if(synchro_context != null) userAccount.setContextInscription(synchro_context);
				}
				if(_represents.getId() != null)
				{
					Resource synchro_represents = em.find(_represents.getClass(), _represents.getId());
					if(synchro_represents != null) userAccount.setRepresents(synchro_represents);
				}
				if(_user.getId() != null)
				{
					Person synchro_user = em.find(_user.getClass(), _user.getId());
					if(synchro_user != null) userAccount.setUser(synchro_user);
				}
				Collection<AgentStatus> synchro_added = new ArrayList<AgentStatus>();
				if(_status != null)
				{
					for(AgentStatus _agent_status : _status)
					{
						if(_agent_status.getId() != null)
						{
							AgentStatus synchro__agent_status = em.find(_agent_status.getClass(), _agent_status.getId());
							if(synchro__agent_status != null) synchro_added.add(synchro__agent_status);
						}
						else synchro_added.add(_agent_status);
					}
				}
				userAccount.setStatus(synchro_added);
				em.persist(userAccount);
				tx.commit();
				return userAccount ;
			}
			catch(Exception e)
			{
				//Et encore un ???
				tx.rollback();
				System.out.println("[DAOUser.createUserAccount] fails to create UserAccount"
						+ " pseudonyme : " + _pseudonyme 
						+ " password : " + _password
						+ " cause : " + e.getMessage());
				return new UserAccount();
			}
		}
		else
		{
			System.out.println("[DAOUser.createUserAccount] fails to create UserAccount"
					+ " pseudonyme : " + _pseudonyme 
					+ " password : " + _password
					+ " resource represents id : " + _represents.getId()
					+ " cause : one of these values is not correct" );
			return new UserAccount() ;
		}
	}
	
	/*RetrieveUserAccount*/
	/**
	 * Retrieves all the UserAccount getting the specified pseudonyme
	 * @param _pseudonyme
	 * @return
	 */
	public List<UserAccount> retrieveUserAccount(String _pseudonyme)
	{
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		try{
			tx.begin();
			List<UserAccount> uas = ((List<UserAccount>)em.createQuery("from UserAccount where pseudonyme = ?").setParameter(1, _pseudonyme).getResultList());
			tx.commit();
			return uas;
		}
		catch(Exception e)
		{
			tx.rollback();
			System.out.println("[DAOUser.retrieveUserAccount] unable to retrieve UserAccount"
					+ " pseudonyme : " + _pseudonyme
					+ " cause : " + e.getMessage());
			return new ArrayList<UserAccount>();
		}
	}
	/**
	 * Retrieves all the UserAccount of a Person.
	 * @param _person
	 * @return
	 */
	public List<UserAccount> retrieveUserAccountFromPerson(Person _person)
	{
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		try{
			tx.begin();
			List<UserAccount> uas = ((List<UserAccount>)em.createQuery("from UserAccount where user = ?").setParameter(1, _person).getResultList());
			tx.commit();
			return uas;
		}
		catch(Exception e)
		{
			tx.rollback();
			System.out.println("[DAOUser.retrieveUserAccountFromPerson] unable to retrieve UserAccount from person :"
					+ " person id : " + _person.getId()
					+ " cause : " + e.getMessage());
			return new ArrayList<UserAccount>();
		}
	}
	/**
	 * Retrieves a UserAccount for a specified application with the specified pseudonyme.
	 * There is only one pseudonyme given an application (there cannot be two users getting the same pseudo in an application).
	 * @param _pseudonyme
	 * @param _application_label
	 * @return
	 */
	public UserAccount retrieveUserAccount(String _pseudonyme , String _application_label)
	{
		//RetrieveApplication _retrieve_app = new RetrieveApplication();
		Application _app = retrieveApplication(_application_label);
		if(_app.getId() != null)
     	{
			EntityManager em = emf.createEntityManager();
			EntityTransaction tx = em.getTransaction();
			try{
				tx.begin();
		        Application _synchro_app = em.find(Application.class, _app.getId());
				if(_synchro_app == null)
				{
					System.out.println("[DAOUser.retrieveUserAccount] unable to retrieve UserAccount"
							+ " pseudonyme : " + _pseudonyme
							+ " application : " + _application_label
							+ " cause : problem retrieving specified application");
					return new UserAccount();	
				}
				UserAccount uas = (UserAccount) em.createQuery("from UserAccount where pseudonyme = ? and context_inscription = ?").setParameter(1, _pseudonyme).setParameter(2, _synchro_app).getSingleResult() ;
				tx.commit();
				return uas;
			}
			catch(Exception e)
			{
				tx.rollback();
				System.out.println("[DAOUser.retrieveUserAccount] unable to retrieve UserAccount"
						+ " pseudonyme : " + _pseudonyme
						+ " application : " + _application_label
						+ " cause : " + e.getMessage());
				return new UserAccount();
			}
     	}
		else
		{
			System.out.println("[DAOUser.retrieveUserAccount] unable to retrieve UserAccount"
					+ " pseudonyme : " + _pseudonyme
					+ " application : " + _application_label
					+ " cause : specified application doesn't exist");
			return new UserAccount();
		}
	}
}