package com.ll.ioc;

import com.ll.domain.testPost.testPost.repository.TestPostRepository;
import com.ll.domain.testPost.testPost.service.TestFacadePostService;
import com.ll.domain.testPost.testPost.service.TestPostService;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * IoC 컨테이너 클론 v1.
 * 빈을 어떻게 만드는지(의존성 포함)는 생성자에서 하드코딩으로 등록하고,
 * genBean 이 처음 불릴 때 만들어서 beans 에 보관한다(싱글톤).
 * 의존 빈은 new 로 직접 만들지 않고 genBean 으로 컨테이너에서 꺼내 주입한다.
 */
public class ApplicationContext {
    // 빈 이름 -> 만들어진 빈(싱글톤)
    private final Map<String, Object> beans = new HashMap<>();
    // 빈 이름 -> 빈을 만드는 방법
    private final Map<String, Supplier<Object>> beanFactories = new HashMap<>();

    public ApplicationContext() {
        beanFactories.put("testPostRepository", TestPostRepository::new);
        beanFactories.put("testPostService", () -> new TestPostService(
                genBean("testPostRepository")
        ));
        beanFactories.put("testFacadePostService", () -> new TestFacadePostService(
                genBean("testPostService"),
                genBean("testPostRepository")
        ));
    }

    @SuppressWarnings("unchecked")
    public <T> T genBean(String beanName) {
        Object bean = beans.get(beanName);

        if (bean == null) {
            Supplier<Object> factory = beanFactories.get(beanName);

            if (factory == null) {
                throw new IllegalArgumentException("등록되지 않은 빈입니다: " + beanName);
            }

            bean = factory.get();
            beans.put(beanName, bean);
        }

        return (T) bean;
    }
}
