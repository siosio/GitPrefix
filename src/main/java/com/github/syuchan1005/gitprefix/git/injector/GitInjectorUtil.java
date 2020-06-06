package com.github.syuchan1005.gitprefix.git.injector;

import com.github.syuchan1005.gitprefix.filetype.PrefixResourceLanguage;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.util.lang.UrlClassLoader;
import git4idea.actions.GitRepositoryAction;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;

import javax.swing.JPanel;

import java.awt.Component;

class GitInjectorUtil {

    static void injectClass(ClassPool classPool, GitInjectorManager.InjectorType type) throws NotFoundException,
            CannotCompileException, IOException {
        CtClass ctClass = classPool.get(type.getInjectClassName());
        ctClass.defrost();

        String project = "project";
        if (type == GitInjectorManager.InjectorType.TAG) {
            project = "myProject";
        }

        String src =
                "public void show() { " +
                        "ClassLoader loader = com.intellij.lang.Language.findLanguageByID(\"" + PrefixResourceLanguage.myID + "\").getClass().getClassLoader();" +
                        "Class clazz = loader.loadClass(\"" + type.getInjectorClassName() + "\");" +
                        "java.lang.reflect.Constructor constructor = clazz.getConstructor(new Class[] {com.intellij.openapi.project.Project.class});"
                        +
                        "Object injector = constructor.newInstance(new Object[] {" + project + "});" +
                        "java.lang.reflect.Method beforeShow = injector.getClass().getMethod(\"beforeShow\", new Class[]{Object.class});"
                        +
                        "java.lang.reflect.Method afterShow  = injector.getClass().getMethod(\"afterShow\" , new Class[]{Object.class});"
                        +
                        "beforeShow.invoke(injector, new Object[] {this});" +
                        "super.show();" +
                        "afterShow.invoke(injector, new Object[] {this});" +
                        "}";
        CtMethod make = CtNewMethod.make(src, ctClass);
        ctClass.addMethod(make);

        ctClass.writeFile();
        ctClass.toClass(GitRepositoryAction.class.getClassLoader(), GitRepositoryAction.class.getProtectionDomain());
    }


	static JPanel getPanel(Object object) throws ReflectiveOperationException {
		Method createCenterPanel = object.getClass().getDeclaredMethod("createCenterPanel");
		createCenterPanel.setAccessible(true);
		JPanel panel = (JPanel) createCenterPanel.invoke(object);
		createCenterPanel.setAccessible(false);
		return panel;
	}
}
