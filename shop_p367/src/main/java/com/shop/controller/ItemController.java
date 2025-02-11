package com.shop.controller;

import com.shop.dto.ItemFormDto;
import com.shop.dto.ItemSearchDto;
import com.shop.entity.Item;
import com.shop.service.ItemService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/admin/item/new")
    public String itemForm(Model model){
        model.addAttribute("itemFormDto", new ItemFormDto());
        return "/item/itemForm";
    }

    @PostMapping("/admin/item/new")
    public String itemForm(@Valid ItemFormDto itemFormDTO, BindingResult bindingResult,
                           Model model, @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList){

        if(bindingResult.hasErrors()){
            return "item/itemForm";
        }

        if(itemImgFileList.get(0).isEmpty() && itemFormDTO.getId() == null){
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력 값 입니다.");
            return "item/itemForm";
        }

        try{
            itemService.saveItem(itemFormDTO, itemImgFileList);
        }catch (Exception e){
            model.addAttribute("errorMessage", "상품 등록 중 에러가 발생했습니다.");
            return "item/itemForm";
        }

        return "redirect:/";
    }

    @GetMapping("/admin/item/{itemId}")
    public String itemDetail(@PathVariable("itemId") Long itemId, Model model){
        try{
            ItemFormDto itemFormDto = itemService.getItemDetail(itemId);
            model.addAttribute("itemFormDto", itemFormDto);
            return "item/itemForm";
        }catch (EntityNotFoundException e){
            model.addAttribute("errorMessage", "존재하지 않는 상품 입니다.");
            return "item/itemForm";
        }
    }

    @PostMapping("/admin/item/{itemId}")
    public String itemUpdate(@Valid ItemFormDto itemFormDto,
             BindingResult bindingResult,
             @RequestParam("itemImgFile") List<MultipartFile> itemImgFilelist, Model model){

        if(bindingResult.hasErrors()){
            return "item/itemForm";
        }

        if(itemImgFilelist.get(0).isEmpty() && itemFormDto.getId() == null){
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력 값 입니다.");
            return "item/itemForm";
        }

        try{
            itemService.updateItem(itemFormDto, itemImgFilelist);
        }catch(Exception e){
            model.addAttribute("errorMessage", "상품 수정 중 엘러가 발생하였습니다.");
        }

        return "redirect:/";
    }

    @GetMapping({"/admin/items", "/admin/ites/{page}"})
    public String itemManage(ItemSearchDto itemSearchDto,
                             @PathVariable("page")Optional<Integer> page, Model model){

        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 3);

        Page<Item> items = itemService.getAdminItemPage(itemSearchDto, pageable);

        model.addAttribute("items", items);
        model.addAttribute("itemSearchDto", itemSearchDto);
        model.addAttribute("maxPage", 5);

        return "item/itemMng";
    }

    @GetMapping("/item/{itemId}")
    public String itemDetail(Model model, @PathVariable("itemId") Long itemId){
        ItemFormDto itemFormDto = itemService.getItemDetail(itemId);
        model.addAttribute("item", itemFormDto);
        return "item/itemDetail";
    }


}