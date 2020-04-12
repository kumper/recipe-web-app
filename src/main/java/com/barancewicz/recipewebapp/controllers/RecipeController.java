package com.barancewicz.recipewebapp.controllers;

import com.barancewicz.recipewebapp.commands.RecipeCommand;
import com.barancewicz.recipewebapp.exceptions.NotFoundException;
import com.barancewicz.recipewebapp.services.RecipeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Controller
public class RecipeController {

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping("recipe/{id}/show")
    public String showById(@PathVariable String id, Model model){
        model.addAttribute("recipe", recipeService.findById(Long.valueOf(id)));
        return "recipe/show";
    }

    @GetMapping("recipe/{id}/delete")
    public String deleteById(@PathVariable String id){
        log.debug("Deleting id: " + id);
        recipeService.deleteById(Long.valueOf(id));
        return "redirect:/";
    }

    //this method will render the view
    @GetMapping("recipe/new")
    public String newRecipe(Model model){
        model.addAttribute("recipe", new RecipeCommand());
        return "recipe/recipeform";
    }
    @GetMapping("recipe/{id}/update")
    public String updateRecipe(@PathVariable String id, Model model){
        model.addAttribute("recipe", recipeService.findCommandById(Long.valueOf(id)));
        return "recipe/recipeform";
    }
    //1. post mapping, refers to html document: th:action="@{/recipe/}"
    // when we get post to th:action="@{/recipe/}", we will invoke this method @RequestMapping("recipe")
    //2. @ModelAttribute this annotation tells spring to bind form attributes to the command object
    //3. We return savedCommand(service implementation) by saving passed command
    @PostMapping("recipe")
    public String saveOrUpdate(@ModelAttribute RecipeCommand command){
        RecipeCommand savedCommand = recipeService.saveRecipeCommand(command);
        return "redirect:/recipe/" + savedCommand.getId() +"/show";
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NumberFormatException.class)
    public ModelAndView handleBadInput(Exception exception){
        log.error("Handling Number Format exception");
        log.error(exception.getMessage());

        ModelAndView modelAndView = new ModelAndView();

        modelAndView.addObject("exception", exception);
        modelAndView.setViewName("400error");

        return modelAndView;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ModelAndView handleNotFound(Exception exception){
        log.error("Handling not found");
        log.error(exception.getMessage());

        ModelAndView modelAndView = new ModelAndView();

        modelAndView.addObject("exception", exception);
        modelAndView.setViewName("404error");

        return modelAndView;
    }
}
