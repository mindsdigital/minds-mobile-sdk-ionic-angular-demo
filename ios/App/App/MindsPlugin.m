//
//  MindsPlugin.m
//  App
//
//  Created by Wennys on 26/04/23.
//

#import <Foundation/Foundation.h>
#import <Capacitor/Capacitor.h>

CAP_PLUGIN(MindsPlugin, "Minds",
    CAP_PLUGIN_METHOD(authentication, CAPPluginReturnPromise);
    CAP_PLUGIN_METHOD(enrollment, CAPPluginReturnPromise);
)

