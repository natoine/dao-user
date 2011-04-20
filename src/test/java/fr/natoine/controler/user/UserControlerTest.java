package fr.natoine.controler.user;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import fr.natoine.dao.user.DAOUser;
import fr.natoine.model_resource.Resource;
import fr.natoine.model_resource.URI;
import fr.natoine.model_user.Agent;
import fr.natoine.model_user.AgentStatus;
import fr.natoine.model_user.Application;
import fr.natoine.model_user.Person;
import fr.natoine.model_user.UserAccount;


import junit.framework.TestCase;

public class UserControlerTest extends TestCase 
{
	private EntityManagerFactory emf_user = Persistence.createEntityManagerFactory("user");
	
	public UserControlerTest(String name) 
	{
	    super(name);
	}
	public void testCreateApplication()
	{
		//CreateApplication _appcontrol = new CreateApplication();
		DAOUser _appcontrol = new DAOUser(emf_user); 
		Resource represents = new Resource();
		represents.setContextCreation("UserControlerTest.testCreateApplication");
		represents.setCreation(new Date());
		represents.setLabel("Resource de testCreateApplication");
		URI representsResource = new URI();
		representsResource.setEffectiveURI("http://test.user.controler.create.application");
		represents.setRepresentsResource(representsResource);
		_appcontrol.createApplication("label", "description", represents);
	}
	
	public void testRetrieveApplication()
	{
		//RetrieveApplication _appcontrol = new RetrieveApplication();
		DAOUser _appcontrol = new DAOUser(emf_user);
		Application _app = _appcontrol.retrieveApplication("label");
		System.out.println("[UserControlerTest.testRetrieveApplication] a retrouvé l'application d'id : " + _app.getId()
			+ " description : " + _app.getDescription() 
			+ " label : " + _app.getLabel() 
			+ " date inscription : " + _app.getInscription());
	}
	
	public void testCreateAgentStatus()
	{
		//sans child
		//CreateAgentStatus _asc = new CreateAgentStatus();
		DAOUser _asc = new DAOUser(emf_user);
		_asc.createAgentStatus("test AgentStatus", "un AgentStatus sans father");
		//avec child
		AgentStatus father = new AgentStatus();
		father.setComment("père");
		father.setLabel("père de test");
		_asc.createAgentStatusChild("test2 AgentStatus", "cette fois avec père", father);
	}
	
	public void testCreateAgent()
	{
		//CreateUser uc = new CreateUser();
		DAOUser uc = new DAOUser(emf_user);
		Application _app = new Application();
		_app.setLabel("UserControlerTestApplication");
		_app.setDescription("userControler test");
		_app.setInscription(new Date());
		Resource representsApp = new Resource();
		representsApp.setContextCreation("UserControlerTestApplication");
		representsApp.setCreation(new Date());
		representsApp.setLabel("resource app usercontroler");
		URI representsResourceApp = new URI();
		representsResourceApp.setEffectiveURI("http://represents.app.test.createAgent");
		representsApp.setRepresentsResource(representsResourceApp);
		_app.setRepresents(representsApp);
		uc.createAgent("agent test", "description d'un agent de test", _app, null);
	}
	
	public void testRetrieveAgent()
	{
		//RetrieveUser ur = new RetrieveUser();
		DAOUser ur = new DAOUser(emf_user);
		List<Agent> _agents = (List<Agent>)ur.retrieveAgent("agent test");
		for(int i = 0 ; i<_agents.size() ; i++)
		{
			System.out.println("[UserControlerTest.testRetrieveAgent] a retrouvé l'agent d'id : " + _agents.get(i).getId()
					+ " label : " + _agents.get(i).getLabel()
					+ " Description : " + _agents.get(i).getDescription()
					+ " ContextInscription : " + _agents.get(i).getContextInscription()
					+ " Date inscription : " + _agents.get(i).getInscription());
		}
	}
	
	public void testCreatePerson()
	{
		//CreateUser uc = new CreateUser();
		DAOUser uc = new DAOUser(emf_user);
		Resource represents = new Resource();
		represents.setContextCreation("UserControlerTest");
		represents.setCreation(new Date());
		represents.setLabel("Resource personne");
		URI representsResource = new URI();
		representsResource.setEffectiveURI("http://uri.resource.representsPerson");
		represents.setRepresentsResource(representsResource);
		Application _app = new Application();
		_app.setLabel("UserControlerTestApplication.testCreatePerson");
		_app.setDescription("userControler test");
		_app.setInscription(new Date());
		Resource representsApp = new Resource();
		representsApp.setContextCreation("UserControlerTestApplication");
		representsApp.setCreation(new Date());
		representsApp.setLabel("resource app usercontroler.testCreatePerson");
		URI representsResourceApp = new URI();
		representsResourceApp.setEffectiveURI("http://represents.app.test.createPerson");
		representsApp.setRepresentsResource(representsResourceApp);
		_app.setRepresents(representsApp);
		uc.createPerson("firstName", "lastName", "mail@gmail.com", "description personne", represents , _app, null);
		//uc.createPerson("firstName", "lastName", "mail@gmail.com", "description personne", represents);
	}
	
	public void testRetrievePerson()
	{
		//RetrieveUser ur = new RetrieveUser();
		DAOUser ur = new DAOUser(emf_user);
		Person _person = ur.retrievePerson("mail@gmail.com");
		System.out.println("[USerControlerTest.testRetrievePerson] personne trouvée id : " + _person.getId()
				+ " contexte inscription : " + _person.getContextInscription()
				+ " description : " + _person.getDescription()
				+ " firstname : " + _person.getFirstName()
				+ " lastname : " + _person.getLastName()
				+ " label : " + _person.getLabel()
				+ " mail : " + _person.getMail()
				+ " date inscription : " + _person.getInscription());
	}
	
	public void testAddStatusToPerson()
	{
		//RetrieveUser ur = new RetrieveUser();
		DAOUser ur = new DAOUser(emf_user);
		Person _person = ur.retrievePerson("mail@gmail.com");
		//CreateAgentStatus _asc = new CreateAgentStatus();
		AgentStatus _status = new AgentStatus();
		_status.setComment("commentaire d'un status de test d'addStatuToAgent");
		_status.setLabel("testAdd");
		ur.addAgentStatusToAgent(_person, _status);
		//RetrieveAgentStatus as = new RetrieveAgentStatus();
		AgentStatus _status2 = ur.retrieveAgentStatus("test AgentStatus");
		ur.addAgentStatusToAgent(_person, _status2);
	}
	
	public void testCreateUserAccount()
	{
		//CreateUser uc = new CreateUser();
		//RetrieveUser ur = new RetrieveUser();
		DAOUser uc = new DAOUser(emf_user);
		Resource represents = new Resource();
		represents.setContextCreation("UserControlerTest.testCreateUSerAccount");
		represents.setCreation(new Date());
		represents.setLabel("resource userAccount");
		URI representsResource = new URI();
		representsResource.setEffectiveURI("http://uri.resource.userAccount");
		represents.setRepresentsResource(representsResource);
		Person user = uc.retrievePerson("mail@gmail.com");
		Application _app = new Application();
		_app.setLabel("UserControlerTestApplication.testuserAccount");
		_app.setDescription("userControler test");
		_app.setInscription(new Date());
		Resource representsApp = new Resource();
		representsApp.setContextCreation("UserControlerTestApplication");
		representsApp.setCreation(new Date());
		representsApp.setLabel("resource app usercontroler.testCreateuserAccount");
		URI representsResourceApp = new URI();
		representsResourceApp.setEffectiveURI("http://represents.app.test.createuserAccount");
		representsApp.setRepresentsResource(representsResourceApp);
		_app.setRepresents(representsApp);
		uc.createUserAccount("pseudonyme", "password", "description", represents, user, _app, null);
		//uc.createUserAccount("UserControlerTest", "password", "pseudonyme", "description", represents, user);
	}
	
	public void testRetrieveUserAccount()
	{
		//RetrieveUser ur = new RetrieveUser();
		DAOUser ur = new DAOUser(emf_user);
		List<UserAccount> useraccounts = (List<UserAccount>)ur.retrieveUserAccount("pseudonyme");
		for(int i =0 ; i<useraccounts.size() ; i++)
		{
			System.out.println("[UserControlerTest.testRetrieveUSerAccount] useraccount trouvé id : " + useraccounts.get(i).getId()
					+ " contexte d'inscription : " + useraccounts.get(i).getContextInscription().getLabel() 
					+ " description : " + useraccounts.get(i).getDescription() 
					+ " label : " + useraccounts.get(i).getLabel()
					+ " pwd : " + useraccounts.get(i).getPassword()
					+ " pseudo : " + useraccounts.get(i).getPseudonyme());
		}
		UserAccount _ua = ur.retrieveUserAccount("pseudonyme", "UserControlerTestApplication.testuserAccount");
		System.out.println("[UserControlerTest.testRetrieveUSerAccount] from pseudo and appliname useraccount trouvé id : " + _ua.getId()
				+ " description : " + _ua.getDescription() 
				+ " label : " + _ua.getLabel()
				+ " pwd : " + _ua.getPassword()
				+ " pseudo : " + _ua.getPseudonyme());
	}
	
	public void testRetrieveUserAccountFromPerson()
	{
		//RetrieveUser ur = new RetrieveUser();
		DAOUser ur = new DAOUser(emf_user);
		Person _person = ur.retrievePerson("mail@gmail.com");
		List<UserAccount> useraccounts = (List<UserAccount>)ur.retrieveUserAccountFromPerson(_person);
		for(int i =0 ; i<useraccounts.size() ; i++)
		{
			System.out.println("[UserControlerTest.testRetrieveUserAccountFromPerson] useraccount trouvé id : " + useraccounts.get(i).getId()
					+ " contexte d'inscription : " + useraccounts.get(i).getContextInscription().getLabel() 
					+ " description : " + useraccounts.get(i).getDescription() 
					+ " label : " + useraccounts.get(i).getLabel()
					+ " pwd : " + useraccounts.get(i).getPassword()
					+ " pseudo : " + useraccounts.get(i).getPseudonyme());
		}
	}
}