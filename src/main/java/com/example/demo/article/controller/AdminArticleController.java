package com.example.demo.article.controller;

import com.example.demo.article.dto.ArticleRequest;
import com.example.demo.article.model.ArticleCategory;
import com.example.demo.article.service.ArticleService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/articles")
public class AdminArticleController {

    private final ArticleService articleService;

    @ModelAttribute("categories")
    public ArticleCategory[] categories() {
        return ArticleCategory.values();
    }

    @GetMapping
    public String dashboard(Model model) {
        if (!model.containsAttribute("articleForm")) {
            model.addAttribute("articleForm", new ArticleRequest());
        }
        model.addAttribute("articles", articleService.getAdminArticles());
        return "admin/dashboard";
    }

    @PostMapping
    public String createArticle(
        @Valid @ModelAttribute("articleForm") ArticleRequest articleForm,
        BindingResult bindingResult,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("articles", articleService.getAdminArticles());
            return "admin/dashboard";
        }

        articleService.createArticle(articleForm);
        redirectAttributes.addFlashAttribute("successMessage", "Article created successfully.");
        return "redirect:/admin/articles";
    }

    @GetMapping("/{id}/edit")
    public String editArticle(@PathVariable Long id, Model model) {
        model.addAttribute("articleId", id);
        model.addAttribute("articleForm", articleService.getArticleForEdit(id));
        return "admin/edit-article";
    }

    @PostMapping("/{id}")
    public String updateArticle(
        @PathVariable Long id,
        @Valid @ModelAttribute("articleForm") ArticleRequest articleForm,
        BindingResult bindingResult,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("articleId", id);
            return "admin/edit-article";
        }

        articleService.updateArticle(id, articleForm);
        redirectAttributes.addFlashAttribute("successMessage", "Article updated successfully.");
        return "redirect:/admin/articles";
    }

    @PostMapping("/{id}/delete")
    public String deleteArticle(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        articleService.deleteArticle(id);
        redirectAttributes.addFlashAttribute("successMessage", "Article deleted successfully.");
        return "redirect:/admin/articles";
    }
}
