package kr.onwork.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * 레이어드 아키텍처 경계 검증 (ADR-SYS-001).
 * web → service → repository → domain 단방향 의존만 허용.
 */
class LayeredArchitectureTest {

    private final JavaClasses classes = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("kr.onwork");

    @Test
    void domainMustNotDependOnOuterLayers() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("..web..", "..service..", "..repository..", "..dto..");
        rule.check(classes);
    }

    @Test
    void repositoryMustNotDependOnServiceOrWeb() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..repository..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("..web..", "..service..");
        rule.check(classes);
    }

    @Test
    void serviceMustNotDependOnWeb() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..service..")
                .should().dependOnClassesThat()
                .resideInAPackage("..web..");
        rule.check(classes);
    }

    @Test
    void controllersMustNotBeAccessedByLowerLayers() {
        ArchRule rule = noClasses()
                .that().resideInAnyPackage("..service..", "..repository..", "..domain..")
                .should().dependOnClassesThat().resideInAPackage("..web..");
        rule.check(classes);
    }
}
