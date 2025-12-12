package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.time.LocalDateTime;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;

@Controller
@RequestMapping("/user") // Все URL этого контроллера начинаются с /user/...
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    // Этот метод выполняется перед КАЖДЫМ обработчиком (@Get/@Post)
    // и добавляет в модель текущего авторизованного пользователя.
    @ModelAttribute
    public void addCommonData(Model model, Principal principal) {
        String userName = principal.getName();                       // email залогиненного пользователя
        User user = userRepository.getUserByUserName(userName);      // достаём пользователя из БД
        model.addAttribute("user", user);                            // кладём в модель, чтобы он был доступен во всех шаблонах
    }

    // Страница личного кабинета (панель пользователя)
    @RequestMapping("/index")
    public String dashboard(Model model) {
        model.addAttribute("title", "Личный кабинет");
        return "normal/user_dashboard"; // шаблон templates/normal/user_dashboard.html
    }

    // Открытие формы "Добавить контакт"
    @GetMapping("/add-contact")
    public String openAddContactForm(Model model) {
        model.addAttribute("title", "Добавить контакт");
        model.addAttribute("contact", new Contact()); // пустой объект для биндинга формы
        return "normal/add_contact_form";
    }

    // Обработка формы "Добавить контакт"
    @PostMapping("/process-contact")
    public String processContact(
            @ModelAttribute Contact contact,                  // данные контакта из формы
            @RequestParam("profileImage") MultipartFile file, // загруженный файл (фото)
            Principal principal,
            HttpSession session) {

        try {
            // Получаем текущего пользователя
            String name = principal.getName();
            User user = this.userRepository.getUserByUserName(name);

            // Если файл не пустой — сохраняем картинку в static/img
            if (!file.isEmpty()) {
                contact.setImage(file.getOriginalFilename()); // имя файла в сущность

                File saveFile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(
                        saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename()
                );

                // Копируем файл в папку ресурса (перезаписываем при совпадении имени)
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }

            // Сохраняем дату создания контакта
            contact.setCreatedAt(LocalDateTime.now());

            // Связываем контакт с пользователем
            contact.setUser(user);
            user.getContacts().add(contact);

            // Сохраняем пользователя вместе с новым контактом
            this.userRepository.save(user);

            // Сообщение об успешном добавлении
            session.setAttribute("message",
                    new Message("Контакт успешно добавлен!", "success"));

        } catch (Exception e) {
            e.printStackTrace();
            // Сообщение об ошибке
            session.setAttribute("message",
                    new Message("Ошибка при добавлении контакта.", "danger"));
        }

        // Возвращаемся на ту же форму
        return "normal/add_contact_form";
    }

    // Показ списка контактов с пагинацией, сортировкой и фильтрами
    @GetMapping("/show-contacts/{page}")
    public String showContacts(@PathVariable("page") Integer page,          // номер страницы
                               @RequestParam(value = "sortField", defaultValue = "name") String sortField,
                               @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir,
                               @RequestParam(value = "hasPhone", required = false) Boolean hasPhone,
                               @RequestParam(value = "hasEmail", required = false) Boolean hasEmail,
                               Model m,
                               Principal principal) {

        // Если чекбоксы не переданы — считаем, что фильтр отключён
        if (hasPhone == null) hasPhone = false;
        if (hasEmail == null) hasEmail = false;

        // Защита: разрешаем сортировать только по этим полям
        if (!sortField.equals("name") &&
                !sortField.equals("email") &&
                !sortField.equals("createdAt")) {
            sortField = "name";
        }

        // Создаём объект Sort (asc/desc)
        Sort sort = Sort.by(sortField);
        sort = sortDir.equalsIgnoreCase("desc") ? sort.descending() : sort.ascending();

        // Находим текущего пользователя
        String userName = principal.getName();
        User user = this.userRepository.getUserByUserName(userName);

        // Параметры страницы: номер + размер + сортировка
        Pageable pageable = PageRequest.of(page, 8, sort);

        Page<Contact> contacts;
        // Если включены фильтры — используем спец. запрос с фильтрацией
        if (hasPhone || hasEmail) {
            contacts = this.contactRepository.findFilteredByUser(
                    user.getId(),
                    hasPhone,
                    hasEmail,
                    pageable
            );
        } else {
            // Иначе просто загружаем контакты пользователя
            contacts = this.contactRepository.findContactsByUser(user.getId(), pageable);
        }

        // Передаём данные в модель для шаблона
        m.addAttribute("contacts", contacts);
        m.addAttribute("currentPage", page);
        m.addAttribute("totalPages", contacts.getTotalPages());
        m.addAttribute("title", "Ваши контакты");

        // Параметры сортировки и фильтров — чтобы их сохранить в интерфейсе
        m.addAttribute("sortField", sortField);
        m.addAttribute("sortDir", sortDir);
        m.addAttribute("hasPhone", hasPhone);
        m.addAttribute("hasEmail", hasEmail);
        m.addAttribute("isSearch", false); // это обычный список, не режим поиска
        m.addAttribute("keyword", "");

        return "normal/show_contacts";
    }

    // Поиск контактов по ключевому слову
    @GetMapping("/search/{page}")
    public String searchContacts(@PathVariable("page") Integer page,
                                 @RequestParam("keyword") String keyword, // то, что ввёл пользователь
                                 Model m,
                                 Principal principal) {

        String userName = principal.getName();
        User user = this.userRepository.getUserByUserName(userName);

        Pageable pageable = PageRequest.of(page, 8);

        // Ищем контакты текущего пользователя по имени/email/телефону/работе
        Page<Contact> contacts =
                this.contactRepository.searchContactsByUser(user.getId(), keyword, pageable);

        // Передаём результаты в шаблон
        m.addAttribute("contacts", contacts);
        m.addAttribute("currentPage", page);
        m.addAttribute("totalPages", contacts.getTotalPages());
        m.addAttribute("title", "Поиск контактов");
        m.addAttribute("isSearch", true);
        m.addAttribute("keyword", keyword);

        // В режиме поиска сортировка и фильтры отключены
        m.addAttribute("sortField", "name");
        m.addAttribute("sortDir", "asc");
        m.addAttribute("hasPhone", false);
        m.addAttribute("hasEmail", false);

        return "normal/show_contacts";
    }

    // Удаление контакта
    @GetMapping("/delete/{cid}")
    public String deleteContact(@PathVariable("cid") Integer cId,
                                Principal principal,
                                HttpSession session) {

        String userName = principal.getName();
        User user = this.userRepository.getUserByUserName(userName);

        // Ищем контакт по id
        Contact contact = this.contactRepository.findById(cId).orElse(null);

        // Удаляем только если контакт принадлежит текущему пользователю
        if (contact != null && contact.getUser().getId() == user.getId()) {
            this.contactRepository.delete(contact);
            session.setAttribute("message", new Message("Контакт удалён.", "success"));
        } else {
            session.setAttribute("message", new Message("Ошибка удаления.", "danger"));
        }

        // После удаления возвращаемся на первую страницу списка контактов
        return "redirect:/user/show-contacts/0";
    }

    // Открытие формы редактирования контакта
    @GetMapping("/update-contact/{cid}")
    public String updateContactForm(@PathVariable("cid") Integer cId,
                                    Model model,
                                    Principal principal,
                                    HttpSession session) {

        String userName = principal.getName();
        User user = this.userRepository.getUserByUserName(userName);
        Contact contact = this.contactRepository.findById(cId).orElse(null);

        // Если контакт не найден или принадлежит другому пользователю — отказ
        if (contact == null || contact.getUser().getId() != user.getId()) {
            session.setAttribute("message",
                    new Message("Контакт не найден или доступ запрещён.", "danger"));
            return "redirect:/user/show-contacts/0";
        }

        model.addAttribute("contact", contact);
        model.addAttribute("title", "Редактировать контакт");

        return "normal/update_contact";
    }

    // Обработка формы обновления контакта
    @PostMapping("/update-contact")
    public String processUpdateContact(@ModelAttribute Contact contact,
                                       Principal principal,
                                       HttpSession session) {

        String userName = principal.getName();
        User user = this.userRepository.getUserByUserName(userName);

        // Восстанавливаем связь контакт → пользователь
        contact.setUser(user);
        this.contactRepository.save(contact);

        session.setAttribute("message", new Message("Контакт обновлён.", "success"));

        return "redirect:/user/show-contacts/0";
    }

    // Страница профиля пользователя
    @GetMapping("/profile")
    public String userProfile(Model model) {
        model.addAttribute("title", "Профиль");
        return "normal/profile";
    }

    // Страница настроек
    @GetMapping("/settings")
    public String userSettings(Model model) {
        model.addAttribute("title", "Настройки");
        return "normal/settings";
    }
}

