package com.softdesign.school.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.softdesign.school.R;
import com.softdesign.school.data.storage.preferenses.UserPreferenses;
import com.softdesign.school.ui.activites.MainActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Ilya_Zelenskiy on 31.01.16.
 */
public class ProfileFragment extends Fragment {

    private static final String FUNCTIONALITY_PROFILE_VIEW = "profile_view";
    private static final String FUNCTIONALITY_PROFILE_EDIT = "profile_edit";
    private static String sCurrentFunctionality = FUNCTIONALITY_PROFILE_VIEW;
    UserPreferenses userFields;
    List<String> mUserProfileData;
    View mainView = null;


    @Bind({R.id.txt_phone_value, R.id.txt_email_value, R.id.txt_vk_value, R.id.txt_git_value, R.id.txt_bio_value})
    List<TextView> txtViewsValues;
    @Bind({R.id.txt_phone_label, R.id.txt_email_label, R.id.txt_vk_label, R.id.txt_git_label, R.id.txt_bio_label})
    List<TextView> txtViewsLabels;
    @Bind({R.id.et_phone_wrapper, R.id.et_email_wrapper, R.id.et_vk_wrapper, R.id.et_git_wrapper, R.id.et_bio_wrapper})
    List<TextInputLayout> etViewsWrappers;
    @Bind({R.id.et_phone_value, R.id.et_email_value, R.id.et_vk_value, R.id.et_git_value, R.id.et_bio_value})
    List<EditText> etViewsValue;

    /**
     * непересоздавать фрагмент при повороте экрана
     */
    public ProfileFragment() {
        this.setRetainInstance(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        userFields = new UserPreferenses();
        mUserProfileData = userFields.loadUserProfileData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (mainView == null) {
            // Если представления нет, создаем его*/
            mainView = inflater.inflate(R.layout.fragment_profile, container, false);
            ButterKnife.bind(this, mainView);
        }

        ((MainActivity) getActivity()).lockAppBar(false, getResources().getString(R.string.drawer_profile_name));


        setFieldsData(txtViewsValues, mUserProfileData); //заполняем View элементы данными
        setFieldsData(etViewsValue, mUserProfileData);
        setupFuncionality(sCurrentFunctionality); //выставляем текущую функциональность
        return mainView;

//        View convertView = inflater.inflate(R.layout.fragment_profile, null, false);
//        getActivity().setTitle(getResources().getString(R.string.drawer_profile_name));
//        AppCompatActivity activity = (AppCompatActivity) getActivity();
//        activity.getSupportActionBar().setTitle(R.string.drawer_profile_name);

//        ((MainActivity) getActivity()).lockAppBar(false, getResources().getString(R.string.drawer_profile_name));
//        return convertView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
        params.setAnchorId(R.id.appbar_layout);
        params.anchorGravity = Gravity.BOTTOM | Gravity.RIGHT;
        fab.setLayoutParams(params);
        fab.setImageResource(R.drawable.ic_mode_edit_24dp);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {                     //создаем и вешаем новый обработчик на fab
                if (sCurrentFunctionality.equals(FUNCTIONALITY_PROFILE_VIEW)) {  //выбираем действие для fab в зависимости от текущего режима
                    setupFuncionality(FUNCTIONALITY_PROFILE_EDIT);
                } else {
                    setupFuncionality(FUNCTIONALITY_PROFILE_VIEW);
                }
            }
        });
    }

    /**
     * Устанавливает текущую функциональность фрагмента
     * @param Funcionality - статическое поле класса с тегом функциональности
     */
    private void setupFuncionality(String Funcionality) {

        sCurrentFunctionality = Funcionality; // выставляем текущую функциональность в зависимости от переданного аргумента

        switch (Funcionality) {
            case FUNCTIONALITY_PROFILE_VIEW: //режим просмотра
                List<String> ScreenData = getFieldsData(etViewsValue);
                if (!mUserProfileData.equals(ScreenData)) {
                    userFields.saveUserProfileData(ScreenData);
                    mUserProfileData = ScreenData;
                    setFieldsData(txtViewsValues, mUserProfileData);
                };
                ButterKnife.apply(etViewsWrappers, INVISIBLE);
                ButterKnife.apply(txtViewsValues, VISIBLE);
                ButterKnife.apply(txtViewsLabels, VISIBLE);

                break;
            case FUNCTIONALITY_PROFILE_EDIT: //режим редактирования
                ButterKnife.apply(etViewsWrappers, VISIBLE);
                ButterKnife.apply(txtViewsValues, INVISIBLE);
                ButterKnife.apply(txtViewsLabels, INVISIBLE);
                break;
            default:
                sCurrentFunctionality = FUNCTIONALITY_PROFILE_VIEW;
                ButterKnife.apply(etViewsWrappers, INVISIBLE);
                ButterKnife.apply(txtViewsValues, VISIBLE);
                ButterKnife.apply(txtViewsLabels, VISIBLE);
                break;
        }

    }

    /**
     * переданные View установить как невидимые
     */
    static final ButterKnife.Action<View> INVISIBLE = new ButterKnife.Action<View>() {
        @Override
        public void apply(View view, int index) {
            view.setVisibility(View.GONE);
        }
    };

    /**
     * переданные View установить как видимые
     */
    static final ButterKnife.Action<View> VISIBLE = new ButterKnife.Action<View>() {
        @Override
        public void apply(View view, int index) {
            view.setVisibility(View.VISIBLE);
        }
    };

    /**
     * вставка массива значений в поля форм
     */
    private void setFieldsData(List<? extends TextView> viewList, List<String> userFields) {
        int i = 0;
        for (TextView viewField : viewList) {
            viewField.setText(userFields.get(i));
            i++;
        }
    }

    /**
     * Получение массива значений из полей форм
     */
    private List<String> getFieldsData(List<? extends TextView> viewList) {
        List<String> userFields = new ArrayList<String>();
        for (TextView viewField : viewList) {
            userFields.add(viewField.getText().toString());
        }
        return userFields;
    }
}
