package follow.developers.plugintest;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import junit.framework.TestCase;

import it.uniba.di.collab.socialcdeforeclipse.action.ActionDynamicUserTimeline;
import it.uniba.di.collab.socialcdeforeclipse.action.ActionLoginPanel;
import it.uniba.di.collab.socialcdeforeclipse.controller.Controller;
import it.uniba.di.collab.socialcdeforeclipse.shared.library.WUser;
import it.uniba.di.collab.socialcdeforeclipse.staticview.LoginPanel;

import org.eclipse.ui.PlatformUI;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class PluginTestUnFollow extends TestCase {

	/**
	 * Unit test for User story number 11.
	 * 
	 * Action considered: Unfollow a developer
	 * 
	 * Equivalence classes considered: 1.Unfollow a developer identified by
	 * correct id
	 * 
	 * Note: For test the equivalence class number 1, the sistem select a random
	 * developer between followings developers. At the end of test the user
	 * selected will be follow.
	 * */

	static HashMap<String, Object> dati;
	Document document;
	WUser[] users;
	String mainViewId; 
	static int positionUser;

	@Before
	public void setUp() throws Exception {

		SAXBuilder builder = new SAXBuilder();
		try {
			document = builder.build(new File("./testData.xml")
					.getCanonicalPath());
		} catch (JDOMException e) {

			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		mainViewId = document.getRootElement().getChild("ViewInfo").getChild("MainView").getChild("Id").getText();

		PlatformUI
		.getWorkbench()
		.getActiveWorkbenchWindow()
		.getActivePage()
		.showView(mainViewId);
		if(Controller.getPreferences("username").equals(""))
		{
			assertNotNull(Controller.getRegistrationPanel()); 
			Controller.getRegistrationPanel().dispose(Controller.getWindow()); 
			Controller.setRegistration_panel(null); 
			Controller.setLoginPanel(new LoginPanel()); 
			Controller.getLoginPanel().inizialize(Controller.getWindow()); 
			assertNotNull(Controller.getLoginPanel()); 
			assertNotNull(Controller.getLoginPanel()); 
			dati = Controller.getLoginPanel().getData();
			dati.put("ID_action", "btnLogin");
			dati.put("Event_type", SWT.Selection);
			((Text) dati.get("txtProxyHost")).setText(document.getRootElement()
					.getChild("CorrectData").getChild("Proxy").getText());
			((Text) dati.get("txtUsername")).setText(document.getRootElement()
					.getChild("CorrectData").getChild("Username").getText());
			((Text) dati.get("txtPassword")).setText(document.getRootElement()
					.getChild("CorrectData").getChild("Password").getText());
			new ActionLoginPanel(dati);
			
			assertNotNull(Controller.getHomeWindow());
		}
		else
		{
			assertNotNull(Controller.getLoginPanel()); 
			dati = Controller.getLoginPanel().getData();
			dati.put("ID_action", "btnLogin");
			dati.put("Event_type", SWT.Selection);
			((Text) dati.get("txtProxyHost")).setText(document.getRootElement()
					.getChild("CorrectData").getChild("Proxy").getText());
			((Text) dati.get("txtUsername")).setText(document.getRootElement()
					.getChild("CorrectData").getChild("Username").getText());
			((Text) dati.get("txtPassword")).setText(document.getRootElement()
					.getChild("CorrectData").getChild("Password").getText());
			new ActionLoginPanel(dati);
			
			assertNotNull(Controller.getHomeWindow());
		}
		users = Controller.getProxy().GetFollowings(
				Controller.getCurrentUser().Username,
				Controller.getCurrentUserPassword());
		assertTrue(users.length > 0);
	}

	@Test
	public void testCase1() {
		positionUser = (int) (Math.random() * (users.length - 1));
		Controller.temporaryInformation.put("User_selected",
				users[positionUser]);
		Controller.temporaryInformation.put("User_type", "Following");
		Controller.selectDynamicWindow(3);
		assertNotNull(Controller.getDynamicUserWindow());
		dati = Controller.getDynamicUserWindow().getData();
		dati.put("ID_action", "labelUnfollow");
		dati.put("Event_type", SWT.Selection);

		assertTrue((Boolean) dati.get("imageFollow"));
		assertFalse((Boolean) dati.get("imageUnFollow"));

		new ActionDynamicUserTimeline(dati);

		assertFalse((Boolean) dati.get("imageUnFollow"));
		assertTrue((Boolean) dati.get("imageFollow"));

		assertFalse((Boolean) dati.get("error"));

	}

	@After
	public void tearDown() throws Exception {

		if (positionUser != -1) {
			assertFalse((Boolean) dati.get("imageUnFollow"));
			assertTrue((Boolean) dati.get("imageFollow"));
			assertTrue(Controller.getProxy()
					.Follow(Controller.getCurrentUser().Username,
							Controller.getCurrentUserPassword(),
							users[positionUser].Id));

		}

		PlatformUI
				.getWorkbench()
				.getActiveWorkbenchWindow()
				.getActivePage()
				.hideView(
						PlatformUI
								.getWorkbench()
								.getActiveWorkbenchWindow()
								.getActivePage()
								.findView(
										mainViewId));
	}

}
