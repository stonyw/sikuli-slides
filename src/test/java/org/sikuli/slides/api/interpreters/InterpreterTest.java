package org.sikuli.slides.api.interpreters;


import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;
import org.sikuli.slides.api.actions.Action;
import org.sikuli.slides.api.actions.BrowserAction;
import org.sikuli.slides.api.actions.DoubleClickAction;
import org.sikuli.slides.api.actions.ExistAction;
import org.sikuli.slides.api.actions.LabelAction;
import org.sikuli.slides.api.actions.LeftClickAction;
import org.sikuli.slides.api.actions.NotExistAction;
import org.sikuli.slides.api.actions.RightClickAction;
import org.sikuli.slides.api.actions.TargetAction;
import org.sikuli.slides.api.actions.TypeAction;
import org.sikuli.slides.api.actions.DelayAction;
import org.sikuli.slides.api.actions.WaitAction;
import org.sikuli.slides.api.interpreters.DefaultInterpreter;
import org.sikuli.slides.api.interpreters.Interpreter;
import org.sikuli.slides.api.interpreters.Keyword;
import org.sikuli.slides.api.interpreters.KeywordDictionary;
import org.sikuli.slides.api.models.ImageElement;
import org.sikuli.slides.api.models.Slide;
import org.sikuli.slides.api.models.SlideElement;

public class InterpreterTest {

	private static final String TEST_TEXT = "some text";
	private DefaultInterpreter interpreter;
	private URL source;
	private Slide slide;

	@Test
	public void testInterpretBrowserAction() throws MalformedURLException{
		URL url = new URL("http://slides.sikuli.org");
		
		Slide slide = new Slide();
		slide.newKeywordElement().keyword(KeywordDictionary.BROWSER).add();
		slide.newElement().text(url.toString()).add();
		
		Interpreter interpreter = new DefaultInterpreter();
		Action action = interpreter.interpret(slide);
		
		assertThat(action, notNullValue());
		assertEquals("browser action", BrowserAction.class, action.getClass());
		
		BrowserAction browserAction = (BrowserAction) action;
		assertNotNull(browserAction.getUrl());
		assertEquals("browser url", url, browserAction.getUrl());
	}
	
	
	private void addTarget(Slide slide){
		ImageElement screenshotElement = new ImageElement();
		screenshotElement.setSource(getClass().getResource("sikuli_context.png"));
		screenshotElement.setOffx(100);
		screenshotElement.setOffy(100);
		screenshotElement.setCx(1000);
		screenshotElement.setCy(1000);
		slide.add(screenshotElement);
		
		SlideElement targetElement = new SlideElement(); 
		targetElement.setOffx(348);
		targetElement.setOffy(223);
		targetElement.setCx(200);
		targetElement.setCy(200);		
		targetElement.setTextSize(3600);
		targetElement.setText(TEST_TEXT);
		slide.add(targetElement);
	}
	
	private Slide createKeywordWithTargetSlide(Keyword keyword){		
		Slide slide = new Slide();
		slide.newKeywordElement().keyword(keyword).add();		
		addTarget(slide);
		return slide;
	}
	
	private Slide createDelaySlide(String text){		
		Slide slide = new Slide();
		slide.newKeywordElement().keyword(KeywordDictionary.DELAY).add();
		slide.newElement().text(text).add();
		return slide;
	}
	
	@Before
	public void setUp() throws IOException{
		interpreter = new DefaultInterpreter();
		slide = new Slide();
		source = getClass().getResource("sikuli_context.png");
	}
	
	@Test
	public void testLeftClickAction(){
		Slide slide = createKeywordWithTargetSlide(KeywordDictionary.CLICK);		
		TargetAction action = (TargetAction) interpreter.interpret(slide);
		
		assertThat(action, notNullValue());
		assertThat(action, instanceOf(TargetAction.class));
		assertThat(action.getTargetAction(), instanceOf(LeftClickAction.class));	
	}
	
	@Test
	public void testRightClickAction() {
		Slide slide = createKeywordWithTargetSlide(KeywordDictionary.RIGHT_CLICK);		
		TargetAction action = (TargetAction) interpreter.interpret(slide);
		
		assertNotNull(action);
		assertThat(action, instanceOf(TargetAction.class));
		assertThat(action.getTargetAction(), instanceOf(RightClickAction.class));
	}
	
	@Test
	public void testDoubleClickAction() {
		Slide slide = createKeywordWithTargetSlide(KeywordDictionary.DOUBLE_CLICK);
		TargetAction action = (TargetAction) interpreter.interpret(slide);
		
		assertNotNull(action);
		assertThat(action.getTargetAction(), instanceOf(DoubleClickAction.class));
	}
	
	@Test
	public void testLabelActionFromOnlyText() {
		Slide slide = new Slide();
		slide.newElement().text("label").add();
		
		LabelAction action = (LabelAction) interpreter.interpret(slide);
		assertThat(action, notNullValue());
	}	
	
	@Test
	public void testLabelActionFromOnlyTextTarget() {
		Slide slide = new Slide();
		slide.newElement().bounds(100,100,50,50).text("label").add();
		slide.newImageElement().source(source).bounds(0,0,200,200).add();
		
		TargetAction action = (TargetAction) interpreter.interpret(slide);
		assertThat(action, notNullValue());		
		assertThat(action.getTargetAction(), instanceOf(LabelAction.class));
	}	
	
	@Test
	public void testExistAction() {
		Slide slide = createKeywordWithTargetSlide(KeywordDictionary.EXIST);
		ExistAction action = (ExistAction) interpreter.interpret(slide);		
		assertThat(action, notNullValue());
	}	

	@Test
	public void testInterpretNotExistAction() {
		Slide slide = createKeywordWithTargetSlide(KeywordDictionary.NOT_EXIST);
		NotExistAction action = (NotExistAction) interpreter.interpret(slide);		
		assertNotNull(action);
	}	
	
	
	@Test
	public void testTypeActionOnTarget() {
		slide = new Slide();
		slide.newKeywordElement().keyword(KeywordDictionary.TYPE).add();		
		slide.newImageElement().source(source).bounds(100,100,50,50).add();
		slide.newElement().text("some text").bounds(120,120,30,30).add();		
		
		TargetAction action = (TargetAction) interpreter.interpret(slide);		
		assertNotNull(action);
		assertThat(action.getTargetAction(), instanceOf(TypeAction.class));
		
		TypeAction typeAction = (TypeAction) action.getTargetAction();
		assertThat(typeAction.getText(), equalToIgnoringCase("some text"));
	}	
	
	@Test
	public void testTypeActionWithTextInKeywordElement() {
		slide = new Slide();
		slide.newKeywordElement().keyword(KeywordDictionary.TYPE).text("some text").add();		
		
		TypeAction action = (TypeAction) interpreter.interpret(slide);		
		assertThat(action, notNullValue());
	}
	
	@Test
	public void testTypeActionWithTextInAnotherElement() {
		slide = new Slide();
		slide.newKeywordElement().keyword(KeywordDictionary.TYPE).add();
		slide.newElement().text("some text").add();
		
		TypeAction action = (TypeAction) interpreter.interpret(slide);		
		assertThat(action, notNullValue());
	}

	@Test
	public void testInterpretWaitAction() {
		Slide slide = new Slide();
		slide.newKeywordElement().keyword(KeywordDictionary.WAIT).add();
		slide.newElement().text("2 seconds").add();
		slide.newImageElement().source(source).bounds(100,100,50,50).add();
		slide.newElement().bounds(120,120,30,30).add();		

		WaitAction action = (WaitAction) interpreter.interpret(slide);		
		assertThat(action, notNullValue());
		assertThat(action.getDuration(), equalTo(2000L));
		assertThat(action.getTarget(), notNullValue());
	}
	
	@Test
	public void testInterpretDelayAction() {
		Slide slide = createDelaySlide("2 seconds");				
		Action action = interpreter.interpret(slide);
		
		assertNotNull(action);
		assertEquals("delay action", DelayAction.class, action.getClass());
		DelayAction a = (DelayAction) action;				
		assertEquals(2000, a.getDuration());
		
		slide = createDelaySlide("2");		
		a = (DelayAction) interpreter.interpret(slide);
		assertEquals(2000, a.getDuration());
		
		slide = createDelaySlide("1 minute");		
		a = (DelayAction) interpreter.interpret(slide);
		assertEquals(1000 * 60, a.getDuration());

		slide = createDelaySlide("0.5 second");		
		a = (DelayAction) interpreter.interpret(slide);
		assertEquals(500, a.getDuration());

		slide = createDelaySlide("0.5");		
		a = (DelayAction) interpreter.interpret(slide);
		assertEquals(500, a.getDuration());

		slide = createDelaySlide("0");		
		a = (DelayAction) interpreter.interpret(slide);
		assertEquals(0, a.getDuration());
		
	}
	
	
	@Test
	public void testInterpretedElementsShouldBeRemoved(){
//		Slide slide = createKeywordWithTargetSlide(KeywordDictionary.CLICK);		
//		TargetAction action = (TargetAction) interpreter.interpret(slide);
//		
//		assertThat(action, notNullValue());
//		assertThat(action, instanceOf(TargetAction.class));
//		assertThat(action.getTargetAction(), instanceOf(LeftClickAction.class));	
	}
}
