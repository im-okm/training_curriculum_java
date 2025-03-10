package in.tech_camp.training_curriculum_java.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import in.tech_camp.training_curriculum_java.entity.PlanEntity;
import in.tech_camp.training_curriculum_java.form.PlanForm;
import in.tech_camp.training_curriculum_java.repository.PlanRepository;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class CalendarsController {

  private final PlanRepository planRepository;

  // 1週間のカレンダーと予定が表示されるページ
  @GetMapping("/")
  public String index(Model model) {
    model.addAttribute("planForm", new PlanForm());
    List<Map<String, Object>> weekDays = getWeek();
    // List<Map<String, Object>> weekDays = get_week(); //スネークケースをキャメルケースに書き換え
    model.addAttribute("weekDays", weekDays);
    return "calendars/index";
  }

  // 予定の保存
  @PostMapping("/calendars")
  public String create(@ModelAttribute("planForm") @Validated PlanForm planForm, BindingResult result) {
    if (!result.hasErrors()) {
      PlanEntity newPlan = new PlanEntity();
      newPlan.setDate(planForm.getDate());
      newPlan.setPlan(planForm.getPlan());
      planRepository.insert(newPlan);
    }
    return "redirect:/";
    // return "redirect:/calendars";　Issue4で修正
  }

  private List<Map<String, Object>> getWeek() {
  // private List<Map<String, Object>> get_week() { //スネークケースをキャメルケースに書き換え
    List<Map<String, Object>> weekDays = new ArrayList<>();

    LocalDate todaysDate = LocalDate.now();
    List<PlanEntity> plans = planRepository.findByDateBetween(todaysDate, todaysDate.plusDays(6));

    String[] wdays = {"(日)", "(月)", "(火)", "(水)", "(木)", "(金)", "(土)"};

    for (int x = 0; x < 7; x++) {
      Map<String, Object> dayMap = new HashMap<>();
      // Map<String, Object> day_map = new HashMap<>(); //スネークケースをキャメルケースに書き換え
      // Map<String, Object> day_map = new HashMap<String, Object>(); //java７以前の記法を改善
      LocalDate currentDate = todaysDate.plusDays(x);

      List<String> todayPlans = new ArrayList<>();
      for (PlanEntity plan : plans) {
          if (plan.getDate().equals(currentDate)) {
              todayPlans.add(plan.getPlan());
          }
      }

      dayMap.put("month", currentDate.getMonthValue());
      dayMap.put("date", currentDate.getDayOfMonth());
      dayMap.put("plans", todayPlans);
      dayMap.put("weekday", wdays[currentDate.getDayOfWeek().getValue() % 7]); //Issue6で追加

      weekDays.add(dayMap);
    }

    return weekDays;
  }


}
