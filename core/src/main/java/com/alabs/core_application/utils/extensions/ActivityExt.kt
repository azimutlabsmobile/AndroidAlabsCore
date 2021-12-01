package com.alabs.core_application.utils.extensions

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.alabs.core_application.data.constants.CoreConstant
import com.alabs.core_application.data.constants.CoreConstant.ARG_ANIM
import com.alabs.core_application.data.constants.CoreFileConstants
import com.alabs.core_application.data.constants.RequestCodeConstants.TAKE_PHOTO
import com.alabs.core_application.data.prefs.SecurityDataSource
import com.alabs.core_application.presentation.model.UIActivityAnimation
import com.alabs.core_application.presentation.ui.activities.CoreActivity
import com.alabs.core_application.utils.delegates.Theme
import com.alabs.core_application.utils.permission.ActivityPermissionImpl
import com.alabs.core_application.utils.permission.Permission
import com.alabs.lingver.Lingver
import org.koin.android.ext.android.inject
import java.io.File
import java.io.IOException
import java.util.*


/**
 * @param activity activity которое нужно открыть
 * @param typeAnimation тип анимации
 * @param arg аргументы
 */
fun <T : Parcelable?> Activity.showActivity(
    activity: Activity,
    typeAnimation: UIActivityAnimation? = null,
    arg: HashMap<String, T>? = hashMapOf()
) {
    startActivity(Intent(this, activity::class.java).apply {
        arg?.forEach {
            putExtra(it.key, it.value)
        }
        putExtra(ARG_ANIM, typeAnimation)
    })

    if (this is CoreActivity) {
        when (typeAnimation) {
            UIActivityAnimation.BOTTOM -> animBottomToTop()
            UIActivityAnimation.RIGHT -> animLeftToRight()
        }
    }
}

/**
 * Открытие фрагмента
 * @param fragment фрагмент который требуеться открыть
 * @param isBackStack фраг который определяет нужет ли стек
 */
fun AppCompatActivity.showFragment(fragment: Fragment, isBackStack: Boolean = true) {
    val fm: FragmentManager = supportFragmentManager
    val tr = fm.beginTransaction()
        .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
        .show(fragment)
    if (isBackStack) {
        tr.addToBackStack(null)
    }
    tr.commit()
}

/**
 * Запрашиваем опасные разрешения
 * @param permissionList список из разрешений
 * @param request код для который можно отследить в onRequestPermissionsResult
 * @param isPermission callback с флагом, полученино ли зарешение
 */
fun AppCompatActivity.requestPermission(
    permissionList: Array<String>,
    request: Int,
    isPermission: ((Boolean) -> Unit?)? = null
) {
    var permission: Permission? = null

    if (permission == null) {
        permission = ActivityPermissionImpl(activity = this)
    }

    permission.apply {
        if (isPermission == null) {
            permissionExistFragment(permissionList, request)
            return
        }
        isPermission.invoke(permissionExistFragment(permissionList, request))
    }
}

/**
 * Переход на другое активити и отчистка стека
 * @param activity на которое требуеться переход
 * @param args аргумент
 */
fun Activity.showActivityAndClearBackStack(
    activity: Activity?,
    args: Bundle? = null
) {
    if (activity == null) throw Throwable("Укажите activity")
    startActivity(Intent(this, activity::class.java).apply {
        args?.let {
            putExtras(it)
        }
    })
    ActivityCompat.finishAffinity(this)
}

/**
 * Переключение темы
 * @param theme в случае Theme.APP стандартная тема приложения в Theme.DARK темная тема приложения
 */
fun Activity.setTheme(theme: Theme) {
    (this as? CoreActivity)?.let {
        setTheme(theme, window)
    }
}

/**
 * Получение темы
 */
fun Activity.getUITheme(): Theme {
    (this as? CoreActivity)?.let {
        return getUITheme()
    }
    throw Exception("Данная функция недоступна")
}

/**
 * Получение темы DARK или LIGHT учитывая системную тему
 * метод выше getUITheme возвращает выбранный пользователем тему (dark, light, system)
 * а внешние сервисы (webview и тд) не могут обрабаботать режим system
 * getUIThemeMode проверяет режим на system и возвращает только dark или light
 */
fun Activity.getUIThemeMode(): Theme {
    (this as? CoreActivity)?.let {
        var theme = getUITheme()

        if (theme == Theme.SYSTEM) {
            theme = if (isDarkThemeOn()) Theme.DARK else Theme.APP
        }

        return theme
    }
    throw Exception("Данная функция недоступна")
}

/**
 * Проверка установления темной темы
 */
fun Activity.isDarkThemeOn(): Boolean {
    return resources.configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
}


/**
 * Проверяем поддерживает ли dark mode устроство
 */
fun Activity.checkSupportDarkMode(): Boolean {
    (this as? CoreActivity)?.let {
        return checkSupportDarkMode()
    }
    return false
}

/**
 * Находит фрагменты для определенного NavHostFragment-a(Контейнера фрагментов)
 * @param fragmentManager FragmentManager
 * @param navHostFragmentId Id navHostFragment-а от которого вы хотите получить фрагменты
 */
fun Activity.getFragmentsFromNavHostById(
    fragmentManager: FragmentManager,
    navHostFragmentId: Int
): List<Fragment>? {
    val navHostFragment = fragmentManager.findFragmentById(navHostFragmentId)
    return navHostFragment?.childFragmentManager?.fragments
}

/**
 * Анимация для активити слайд снизу вверх
 */
fun Activity.animBottomToTop() {
    if (this is CoreActivity) {
        animBottomToTop()
    }
}

/**
 * Анимация для активити слайд сверху вниз
 */
fun Activity.animTopToBottom() {
    if (this is CoreActivity) {
        animTopToBottom()
    }
}

/**
 * Анимация для активити слайд слева вправо
 */
fun Activity.animLeftToRight() {
    if (this is CoreActivity) {
        animLeftToRight()
    }
}

/**
 * Анимация для активити слайд справа налево
 */
fun Activity.rightToLeft() {
    if (this is CoreActivity) {
        rightToLeft()
    }
}

/**
 * Сделать статус бар прозрачным
 */
fun Activity.setupStatusBar() {
    window?.apply {
        clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.TRANSPARENT
        }
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }
}

/**
 * Открывает Google Play App по packageName
 */
fun Activity.openGooglePlayByPackageName(packageName: String) {
    val googlePlayIntent = try {
        Intent(
            Intent.ACTION_VIEW,
            Uri.parse("market://details?id=$packageName")
        )
    } catch (anfe: ActivityNotFoundException) {
        Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
        )
    }
    startActivity(googlePlayIntent)
}

/**
 * Проверяет установлено ли приложении на устройстве по packageName
 */
fun Activity.isAppInstalled(packageName: String): Boolean {
    return try {
        packageManager.getPackageInfo(packageName, 0)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }
}


/**
 * Открытие Activity с другого модуля
 * @param path полный путь к пакету где разположено Activity
 */
fun Activity.showModuleActivity(path: String) {
    var intent: Intent? = null
    try {
        intent = Intent(
            this,
            Class.forName(path)
        )
        startActivity(intent)
    } catch (e: ClassNotFoundException) {
        e.printStackTrace()
    }
}

/**
 * Открытие Activity с другого модуля c аргументами
 * @param path полный путь к пакету, где разположено Activity
 * @param args аргументы
 */
fun <T : Parcelable> Activity.showModuleActivity(
    path: String,
    args: HashMap<String, T>? = hashMapOf()
) {
    try {
        val intent = Intent(this, Class.forName(path))
        args?.forEach {
            intent.putExtra(it.key, it.value)
        }
        startActivity(intent)
    } catch (e: ClassNotFoundException) {
        e.printStackTrace()
    }
}

/**
 * @param activity activity которое нужно открыть
 * @param arg аргументы
 */
fun Activity.showActivity(
    activity: Activity,
    args: Bundle? = null,
    typeAnimation: UIActivityAnimation? = null
) {
    startActivity(Intent(this, activity::class.java).apply {
        args?.let {
            putExtras(it)
        }
        putExtra(ARG_ANIM, typeAnimation)
    })

    if (this is CoreActivity) {
        when (typeAnimation) {
            UIActivityAnimation.BOTTOM -> animBottomToTop()
            UIActivityAnimation.RIGHT -> animLeftToRight()
        }
    }
}

/**
 * Открытие Activity с другого модуля c аргументами
 * @param path полный путь к пакету, где разположено Activity
 * @param args аргументы
 */
fun Activity.showModuleActivity(
    path: String,
    args: Bundle? = null,
    typeAnimation: UIActivityAnimation? = null
) {
    try {
        val intent = Intent(this, Class.forName(path))
        args?.let {
            intent.putExtras(it)
        }
        startActivity(intent)

        if (this is CoreActivity) {
            when (typeAnimation) {
                UIActivityAnimation.BOTTOM -> animBottomToTop()
                UIActivityAnimation.RIGHT -> animLeftToRight()
            }
        }
    } catch (e: ClassNotFoundException) {
        e.printStackTrace()
    }
}

/**
 * Открытие Activity с другого модуля через [startActivityForResult] работа с результатом
 * @param path полный путь к пакету Activity
 * @param request код
 * @param args аргументы
 */
fun <T : Parcelable> Activity.showModuleActivityForResult(
    path: String,
    request: Int = Activity.RESULT_FIRST_USER,
    args: HashMap<String, T>? = hashMapOf()
) {
    try {
        val intent = Intent(this, Class.forName(path))
        args?.forEach {
            intent.putExtra(it.key, it.value)
        }
        startActivityForResult(intent, request, null)
    } catch (e: ClassNotFoundException) {
        e.printStackTrace()
    }
}


/**
 * Открытие Activity с другого модуля с чисткой back stack-а
 * @param path полный путь к пакету где разположено Activity
 */
fun Activity.showModuleActivityAndClearBackStack(path: String, args: Bundle? = null) {
    try {
        startActivity(Intent(
            this,
            Class.forName(path)
        ).apply {
            args?.let {
                putExtras(it)
            }
        })
        ActivityCompat.finishAffinity(this)
    } catch (e: ClassNotFoundException) {
        e.printStackTrace()
    }
}

/**
 * Получение Bitmap через URI
 * @param uri изображение
 * @return bitmap
 */
fun Activity.getBitmapFromUri(data: Uri?): Bitmap? =
    MediaStore.Images.Media.getBitmap(contentResolver, data)


/**
 *  Позволяет делиться файлом с другим приложением
 *  @param file файл который необходимо отправить в другое приложение
 *  @param pkg пакет в котором происходит деление файла например com.app.application
 *  @param typeFile тип файла например application/pdf
 *
 */
fun Activity.shareFile(
    file: File,
    pkg: String,
    typeFile: String
) {
    val uri = FileProvider.getUriForFile(this, pkg, file)
    val shareIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_STREAM, uri)
        type = typeFile
    }
    startActivity(Intent.createChooser(shareIntent, null))
}

/**
 * Открывает экранно ввода номера телефона и автоматически заполняет указзаный номер телефона
 * @param phoneNumber номер телефона
 */
fun Activity.makeCall(phoneNumber: String?) {
    val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber.orEmpty(), null))
    startActivity(intent)
}

/**
 * Открывает ссылку в браузере или приложение если есть
 * @param value ссылка
 */
fun Activity.openUrl(value: String?) {
    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(value)))
}

/**
 * Изменение локализации в приложении
 * @param locale локализация
 */
fun Activity.changeLocale(locale: Locale, isAnimationRecreate: Boolean) {
    Lingver.getInstance().setLocale(this, locale)

    if (isAnimationRecreate) {
        recreateActivity(true)
    } else {
        recreate()
    }
}

/**
 * возможность перезагрузить Activity с анимацией
 *  @param isAnimate - true анимировано перезагружает Activity
 */
fun Activity.recreateActivity(isAnimate: Boolean = false) {
    val restartIntent = Intent(this, this.javaClass)

    val extras = intent?.extras
    if (extras != null) {
        restartIntent.putExtras(extras)
    }

    if (isAnimate) {
        ActivityCompat.startActivity(
            this,
            restartIntent,
            ActivityOptionsCompat
                .makeCustomAnimation(this, android.R.anim.fade_in, android.R.anim.fade_out)
                .toBundle()
        )
    } else {
        startActivity(restartIntent)
        overridePendingTransition(0, 0)
    }

    finish()
}

/**
 * Позволяет делиться текстом с заданным заголовком для chooser-a
 */
fun Activity.shareText(
    text: String,
    chooserText: String
) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }
    val chooser = Intent.createChooser(shareIntent, chooserText)
    startActivity(chooser)
}

/**
 * Открываем стандартную камеру делает снимок и возвращает файл
 */
fun Activity.showCameraForPictureImage(authority: String): File {
    var file = File("")
    Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
        takePictureIntent.resolveActivity(packageManager ?: return@also)?.also {
            file = try {
                createFile("JPEG_${System.currentTimeMillis()}_", CoreFileConstants.EXTENSION_JPG)

            } catch (ex: IOException) {
                File(CoreConstant.EMPTY)
            }
            file.also {
                val photoURI: Uri = FileProvider.getUriForFile(
                    this,
                    authority,
                    it
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, TAKE_PHOTO)
            }
        }
    }
    return file
}


/**
 * Открытие Activity с другого модуля c аргументами
 * @param path полный путь к пакету, где разположено Activity
 * @param args аргументы
 * @param isAuth true в случае проверки на авторизацию
 * Полезно когда экран требует авторизацию и треубуеться войти в него уже с актуальным token-ом
 */
fun Activity.authShowModuleActivity(
    path: String,
    args: Bundle? = null,
    isAuth: Boolean = false,
    typeAnimation: UIActivityAnimation = UIActivityAnimation.RIGHT
) {

    if (this !is CoreActivity) {
        throw Exception("Родительское активити должно быть CoreActivity")
    }
    val authToken by inject<SecurityDataSource>()
    try {
        val intent = Intent(this, Class.forName(path))
        args?.let {
            intent.putExtras(it)
        }
        if (isAuth && authToken.getAccessToken().isNullOrEmpty()) {
            redirectLogin()
            return
        }
        startActivity(intent)
    } catch (e: ClassNotFoundException) {
        e.printStackTrace()
    }
}