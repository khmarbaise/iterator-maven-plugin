package com.soebes.maven.plugins.itexin;

import static org.fest.assertions.Assertions.assertThat;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;

import org.apache.maven.shared.invoker.InvocationRequest;
import org.mockito.Mockito;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class AbstractInvokerMojoTest {

    private AbstractInvokerMojo mock;

    @BeforeMethod
    public void beforeMethod() {
        mock = Mockito.mock(AbstractInvokerMojo.class, Mockito.CALLS_REAL_METHODS);
        mock.setIteratorName("item");
        mock.setBeginToken("@");
        mock.setEndToken("@");
    }

    @Test
	public void checkReplacementInBaseDirectory() {
	    mock.setBaseDirectory(new File("first-@item@"));

	    InvocationRequest createAndConfigureAnInvocationRequest = mock.createAndConfigureAnInvocationRequest("one");

	    assertThat(createAndConfigureAnInvocationRequest.getBaseDirectory()).isEqualTo(new File("first-one"));
	}

    @Test
    public void checkReplacementInGoal() {
        mock.setGoals(Collections.singletonList("java:@item@-environment"));

        InvocationRequest createAndConfigureAnInvocationRequest = mock.createAndConfigureAnInvocationRequest("one");

        assertThat(createAndConfigureAnInvocationRequest.getGoals()).hasSize(1);
        assertThat(createAndConfigureAnInvocationRequest.getGoals()).isEqualTo(Collections.singletonList("java:one-environment"));
    }

    @Test
    public void checkReplacementInGoals() {
        mock.setGoals(Arrays.asList("java:@item@-environment", "@item@", "selection-@item@-choice"));

        InvocationRequest createAndConfigureAnInvocationRequest = mock.createAndConfigureAnInvocationRequest("one");

        assertThat(createAndConfigureAnInvocationRequest.getGoals()).hasSize(3);
        assertThat(createAndConfigureAnInvocationRequest.getGoals()).isEqualTo(Arrays.asList("java:one-environment", "one", "selection-one-choice"));
    }
    
    @Test
    public void checkReplacementInProfiles() {
        mock.setProfiles(Collections.singletonList("profile-@item@"));

        InvocationRequest createAndConfigureAnInvocationRequest = mock.createAndConfigureAnInvocationRequest("two");

        assertThat(createAndConfigureAnInvocationRequest.getProfiles()).hasSize(1);
        assertThat(createAndConfigureAnInvocationRequest.getProfiles()).isEqualTo(Collections.singletonList("profile-two"));
    }

    @Test
    public void checkReplacementInProjects() {
        mock.setProjects(Collections.singletonList("project-@item@-a"));

        InvocationRequest createAndConfigureAnInvocationRequest = mock.createAndConfigureAnInvocationRequest("three");

        assertThat(createAndConfigureAnInvocationRequest.getProjects()).hasSize(1);
        assertThat(createAndConfigureAnInvocationRequest.getProjects()).isEqualTo(Collections.singletonList("project-three-a"));
    }

}
