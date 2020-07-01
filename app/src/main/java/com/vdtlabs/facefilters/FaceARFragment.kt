package com.vdtlabs.facefilters

import android.os.Bundle
import android.view.View
import com.google.ar.core.Config
import com.google.ar.core.Session
import com.google.ar.sceneform.ux.ArFragment
import java.util.*

/** Created by Vinod Dirishala on 01-07-2020 10:45 **/

class  FaceARFragment : ArFragment(){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        planeDiscoveryController.hide()  // Since we do not need the plane to be detected since this is the face recognizing model
        planeDiscoveryController.setInstructionView(null) // setting plane detecting view to null as we do not need the plane for face model
    }

    override fun getSessionFeatures(): MutableSet<Session.Feature> {
        return EnumSet.of(Session.Feature.FRONT_CAMERA) // Since this is the face detection ar fragment we do not need the back camera, So adding Feature.Face_Camera will use only front camera to detect the face.
    }

    override fun getSessionConfiguration(session: Session?): Config {
        val config = Config(session)
        config.augmentedFaceMode = Config.AugmentedFaceMode.MESH3D // setting this line to detect the faces from the camera
        return config
    }

}