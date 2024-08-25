package com.loren.redbook.data.di

import com.loren.redbook.data.R
import com.loren.redbook.data.bean.UserBean
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier

@Module
@InstallIn(SingletonComponent::class)
object MockDataModule {

    @Qualifier
    annotation class MockContentPic

    @Qualifier
    annotation class MockUserPic

    @Provides
    @MockContentPic
    fun providePictures() = listOf(
        R.drawable.pic_analysis,
        R.drawable.pic_check,
        R.drawable.pic_clound,
        R.drawable.pic_communication,
        R.drawable.pic_cross,
        R.drawable.pic_debate,
        R.drawable.pic_explain,
        R.drawable.pic_hhqy,
        R.drawable.pic_jiaoyou,
        R.drawable.pic_jump,
        R.drawable.pic_liugou,
        R.drawable.pic_lovely,
        R.drawable.pic_playful,
        R.drawable.pic_report,
        R.drawable.pic_research,
        R.drawable.pic_shopping,
        R.drawable.pic_sport,
        R.drawable.pic_team,
        R.drawable.pic_txwm,
        R.drawable.pic_vitality,
        R.drawable.pic_wink,
        R.drawable.pic_wpjl
    )

    @Provides
    @MockUserPic
    fun provideUserPictures() = listOf(
        R.drawable.icon_bangbangtang,
        R.drawable.icon_baomihua,
        R.drawable.icon_binggan,
        R.drawable.icon_bingqilin,
        R.drawable.icon_buding,
        R.drawable.icon_danta,
        R.drawable.icon_diaoyubing,
        R.drawable.icon_guaizhangtang,
        R.drawable.icon_hanbao,
        R.drawable.icon_hongjiu,
        R.drawable.icon_huoji,
        R.drawable.icon_jiroujuan,
        R.drawable.icon_kafei,
        R.drawable.icon_kele,
        R.drawable.icon_launcher,
        R.drawable.icon_naicha,
        R.drawable.icon_paobing,
        R.drawable.icon_pisa,
        R.drawable.icon_sanmingzhi,
        R.drawable.icon_take,
        R.drawable.icon_tianfuluo,
        R.drawable.icon_xuegao,
    )

    @Provides
    fun provideUsers() = listOf(
        UserBean("https://huiji-public.huijistatic.com/xinglugu/uploads/0/04/Alex.png", "亚历克斯"),
        UserBean("https://huiji-public.huijistatic.com/xinglugu/uploads/8/88/Abigail.png", "阿比盖尔"),
        UserBean("https://huiji-public.huijistatic.com/xinglugu/uploads/b/bd/Elliott.png", "艾利欧特"),
        UserBean("https://huiji-public.huijistatic.com/xinglugu/uploads/2/28/Emily.png", "艾米丽"),
        UserBean("https://huiji-public.huijistatic.com/xinglugu/uploads/9/95/Harvey.png", "哈维"),
        UserBean("https://huiji-public.huijistatic.com/xinglugu/uploads/1/1b/Haley.png", "海莉"),
        UserBean("https://huiji-public.huijistatic.com/xinglugu/uploads/9/94/Sam.png", "山姆"),
        UserBean("https://huiji-public.huijistatic.com/xinglugu/uploads/e/e6/Leah.png", "莉亚"),
        UserBean("https://huiji-public.huijistatic.com/xinglugu/uploads/a/a8/Sebastian.png", "塞巴斯蒂安"),
        UserBean("https://huiji-public.huijistatic.com/xinglugu/uploads/f/f8/Maru.png", "玛鲁"),
        UserBean("https://huiji-public.huijistatic.com/xinglugu/uploads/8/8b/Shane.png", "谢恩"),
        UserBean("https://huiji-public.huijistatic.com/xinglugu/uploads/a/ab/Penny.png", "潘妮"),
        UserBean("https://huiji-public.huijistatic.com/xinglugu/uploads/8/87/Caroline.png", "卡洛琳"),
        UserBean("https://huiji-public.huijistatic.com/xinglugu/uploads/c/c7/Wizard.png", "法师"),
        UserBean("https://huiji-public.huijistatic.com/xinglugu/uploads/f/f5/Bouncer.png", "门卫"),
        UserBean("https://huiji-public.huijistatic.com/xinglugu/uploads/b/b4/Mr._Qi.png", "齐先生"),
        UserBean("https://huiji-public.huijistatic.com/xinglugu/uploads/4/4e/Sandy.png", "桑迪"),
        UserBean("https://huiji-public.huijistatic.com/xinglugu/uploads/2/2b/Lewis.png", "刘易斯"),
        UserBean("https://huiji-public.huijistatic.com/xinglugu/uploads/7/7e/Pierre.png", "皮埃尔"),
        UserBean("https://huiji-public.huijistatic.com/xinglugu/uploads/3/31/Linus.png", "莱纳斯"),
        UserBean("https://huiji-public.huijistatic.com/xinglugu/uploads/1/1b/Robin.png", "罗宾"),
        UserBean("https://huiji-public.huijistatic.com/xinglugu/uploads/5/52/Marnie.png", "玛妮")
    )
}