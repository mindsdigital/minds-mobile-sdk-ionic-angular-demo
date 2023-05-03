# Configuração do Projeto


Para integrar a SDK da Minds no seu projeto iOS, é necessário seguir os seguintes passos:

- Abra o seu projeto no Xcode e selecione o target do seu aplicativo.
- Clique na aba "Swift Packages" na parte superior da janela.
- Clique no botão "+" no canto inferior esquerdo da janela para adicionar um pacote.
- Na caixa de diálogo que aparece, cole a URL do repositório da SDK da Minds: https://github.com/mindsdigital/minds-sdk-mobile-ios.git
- Certifique-se de que o destino esteja selecionado corretamente e clique em "Finish".
- Aguarde a importação da SDK ser concluída.


# Pré-requisitos

Antes de utilizar este trecho de código, é necessário ter instalado e configurado o Capacitor em seu projeto. Além disso, é preciso ter um controlador de visualização raiz (root view controller) para a aplicação.

# Funcionamento

Este trecho de código inicializa o Capacitor e configura o controlador de visualização raiz para a aplicação. Ele faz isso chamando a função `application(_:didFinishLaunchingWithOptions:)` do protocolo `UIApplicationDelegate`.

Primeiro, a função obtém a referência para o controlador de visualização raiz do aplicativo através da propriedade `rootViewController` da janela do aplicativo. Em seguida, cria uma instância de `UINavigationController` com o controlador de visualização raiz como seu primeiro controlador de visualização e esconde a barra de navegação. Por fim, define a instância do `UINavigationController` como a nova controladora raiz da janela do aplicativo.

Adicione o código abaixo na implementação do método application:

```swift
import UIKit
import Capacitor

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {

    var window: UIWindow?
    var navigationController: UINavigationController?

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        let controller = window?.rootViewController as? CAPBridgeViewController
        navigationController = UINavigationController(rootViewController: controller!)
        navigationController?.navigationBar.isHidden = true
        window?.rootViewController = navigationController
        return true
    }

    func applicationWillResignActive(_ application: UIApplication) {
        // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
        // Use this method to pause ongoing tasks, disable timers, and invalidate graphics rendering callbacks. Games should use this method to pause the game.
    }

    func applicationDidEnterBackground(_ application: UIApplication) {
        // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
        // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
    }

    func applicationWillEnterForeground(_ application: UIApplication) {
        // Called as part of the transition from the background to the active state; here you can undo many of the changes made on entering the background.
    }

    func applicationDidBecomeActive(_ application: UIApplication) {
        // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
    }

    func applicationWillTerminate(_ application: UIApplication) {
        // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
    }

    func application(_ app: UIApplication, open url: URL, options: [UIApplication.OpenURLOptionsKey: Any] = [:]) -> Bool {
        // Called when the app was launched with a url. Feel free to add additional processing here,
        // but if you want the App API to support tracking app url opens, make sure to keep this call
        return ApplicationDelegateProxy.shared.application(app, open: url, options: options)
    }

    func application(_ application: UIApplication, continue userActivity: NSUserActivity, restorationHandler: @escaping ([UIUserActivityRestoring]?) -> Void) -> Bool {
        // Called when the app was launched with an activity, including Universal Links.
        // Feel free to add additional processing here, but if you want the App API to support
        // tracking app url opens, make sure to keep this call
        return ApplicationDelegateProxy.shared.application(application, continue: userActivity, restorationHandler: restorationHandler)
    }

}
```

# Criação do plugin em swift

- Crie o arquivo swift com o nome de "MindspLG"


