package teamcity.api;

import org.apache.http.HttpStatus;
import org.example.teamcity.api.models.BuildType;
import org.example.teamcity.api.models.Project;
import org.example.teamcity.api.models.Roles;
import org.example.teamcity.api.models.User;
import org.example.teamcity.api.requests.CheckedRequests;
import org.example.teamcity.api.requests.unchecked.UncheckedBase;
import org.example.teamcity.api.spec.Specifications;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import java.util.Collections;

import static org.example.teamcity.api.enums.Endpoint.*;
import static org.example.teamcity.api.generators.TestDataGenerator.generate;

@Test(groups = {"Regression"})
public class BuildTypeTest extends BaseApiTest {
    @Test(description = "User should be able to create build type", groups = {"Positive", "CRUD"})
    public void userCreatesBuildTypeTest() {

        superUserCheckRequests.getRequest(PROJECTS).create(testData.getProject());

        testData.getUser().setRoles(generate(Roles.class, "SYSTEM_ADMIN", "g:" + testData.getProject().getId()));

        superUserCheckRequests.getRequest(USERS).create(testData.getUser());

        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        userCheckRequests.getRequest(BUILD_TYPES).create(testData.getBuildType());

        var createdBuildType = userCheckRequests.<BuildType>getRequest(BUILD_TYPES).read(testData.getBuildType().getId());

        softy.assertEquals(testData.getBuildType().getName(), createdBuildType.getName(), "Build type name is not correct");
    }

    @Test(description = "User should not be able to create two build types with the same id", groups = {"Negative", "CRUD"})
    public void userCreatesTwoBuildTypesWithTheSameIdTest() {

        superUserCheckRequests.getRequest(PROJECTS).create(testData.getProject());

        testData.getUser().setRoles(generate(Roles.class, "SYSTEM_ADMIN", "g:" + testData.getProject().getId()));

        var buildTypeWithSameId = generate(Collections.singletonList(testData.getProject()), BuildType.class, testData.getBuildType().getId());

        superUserCheckRequests.getRequest(USERS).create(testData.getUser());

        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        userCheckRequests.getRequest(BUILD_TYPES).create(testData.getBuildType());

        new UncheckedBase(Specifications.authSpec(testData.getUser()), BUILD_TYPES)
                .create(buildTypeWithSameId)
                .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString("The build configuration / template ID \"%s\" is already used by another configuration or template".formatted(testData.getBuildType().getId())));
    }

    @Test(description = "Project admin should be able to create build type for their project", groups = {"Positive", "Roles"})
    public void projectAdminCreatesBuildTypesTest() {

        superUserCheckRequests.getRequest(PROJECTS).create(testData.getProject());

        testData.getUser().setRoles(generate(Roles.class, "PROJECT_ADMIN", "p:" + testData.getProject().getId()));

        superUserCheckRequests.getRequest(USERS).create(testData.getUser());

        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        userCheckRequests.getRequest(BUILD_TYPES).create(testData.getBuildType());

        var createdBuildType = userCheckRequests.<BuildType>getRequest(BUILD_TYPES).read(testData.getBuildType().getId());

        softy.assertEquals(testData.getBuildType().getName(), createdBuildType.getName(), "Build type name is not correct");
    }

    @Test(description = "Project admin should not be able to create build type for another user project", groups = {"Negative", "Roles"})
    public void projectAdminCreatesBuildTypesForAnotherUserProjectTest() {
        superUserCheckRequests.getRequest(PROJECTS).create(testData.getProject());

        testData.getUser().setRoles(generate(Roles.class, "PROJECT_ADMIN", "p:" + testData.getProject().getId()));

        superUserCheckRequests.getRequest(USERS).create(testData.getUser());

        new CheckedRequests(Specifications.authSpec(testData.getUser()));

        var project2 = generate(Project.class, testData.getProject().getId());
        superUserCheckRequests.getRequest(PROJECTS).create(project2);

        testData.getUser().setRoles(generate(Roles.class, "PROJECT_ADMIN", "p:" + testData.getProject().getId()));

        var user2 = generate(User.class, testData.getUser().getId());
        superUserCheckRequests.getRequest(USERS).create(user2);
        new CheckedRequests(Specifications.authSpec(testData.getUser()));

        var buildTypeForAnotherProject = generate(Collections.singletonList(
                testData.getProject()), BuildType.class, testData.getBuildType().getId());

        new UncheckedBase(Specifications.authSpec(user2), BUILD_TYPES)
                .create(buildTypeForAnotherProject)
                .then().assertThat().statusCode(HttpStatus.SC_FORBIDDEN)
                .body(Matchers.containsString("You do not have enough permissions to edit project with id: %s".formatted(testData.getProject().getId())));
    }
}
