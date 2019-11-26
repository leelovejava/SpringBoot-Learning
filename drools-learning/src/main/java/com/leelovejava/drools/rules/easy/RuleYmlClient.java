package com.leelovejava.drools.rules.easy;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.jeasy.rules.core.RulesEngineParameters;
import org.jeasy.rules.mvel.MVELRuleFactory;
import org.jeasy.rules.support.RuleDefinitionReader;
import org.jeasy.rules.support.YamlRuleDefinitionReader;
import org.mvel2.ParserContext;

import java.io.FileReader;

/**
 * @author leelovejava
 * @date 2019/11/24
 */
public class RuleYmlClient {

    public static void main(String[] args) throws Exception {
        // create a rules engine
        RulesEngineParameters parameters = new RulesEngineParameters().skipOnFirstAppliedRule(true);
        RulesEngine fizzBuzzEngine = new DefaultRulesEngine(parameters);

        // create rules
        RuleDefinitionReader ruleDefinitionReader = new YamlRuleDefinitionReader();
        MVELRuleFactory mvelRuleFactory = new MVELRuleFactory(ruleDefinitionReader);
        ParserContext parserContext = ParserContext.create();
        Rules rules = mvelRuleFactory.createRules(new FileReader("fizzbuzz.yml"), parserContext);

        // fire rules
        Facts facts = new Facts();
        for (int i = 1; i <= 100; i++) {
            facts.put("number", i);
            fizzBuzzEngine.fire(rules, facts);
            System.out.println(rules);
        }
    }
}