package com.vdtlabs.facefilters

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.ar.core.AugmentedFace
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.rendering.Texture
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.AugmentedFaceNode
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.CompletableFuture

class MainActivity : AppCompatActivity() {

    private lateinit var arFragment : ArFragment

    private var faceRenderable : ModelRenderable? = null
    private var  faceTexture : Texture? = null

    private var faceNodeMap = HashMap<AugmentedFace, AugmentedFaceNode>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        arFragment = faceFrament as ArFragment
        loadModel() // calling this method to load our model

        arFragment.arSceneView.cameraStreamRenderPriority = Renderable.RENDER_PRIORITY_FIRST
        arFragment.arSceneView.scene.addOnUpdateListener { // call the addTrackedFaces and removeUntrackedFaces for each and every frame
            if (faceRenderable != null && faceTexture != null){
                addTracedFaces()
                removeUnTrackedFaces()
            }
        }
    }

    private fun addTracedFaces(){
        val sesion = arFragment.arSceneView.session ?: return
        val  faceList = sesion.getAllTrackables(AugmentedFace::class.java)
        for (face in faceList){
          if (!faceNodeMap.containsKey(face)){
              AugmentedFaceNode(face).apply {
                  setParent(arFragment.arSceneView.scene)
                  faceRegionsRenderable = faceRenderable
                  faceMeshTexture = faceTexture
                  faceNodeMap[face] = this

              }
          }
        }
    }

    private fun removeUnTrackedFaces(){
        val enteries = faceNodeMap.entries
        for (entry in enteries){
            val face = entry.key
            if (face.trackingState == TrackingState.STOPPED){
                val  focusNode = entry.value
                focusNode.setParent(null)
                enteries.remove(entry)
            }
        }
    }


    private fun loadModel(){
        val  modelRenderable = ModelRenderable.builder()  // load the fox face material sfb file
            .setSource(this,R.raw.fox_face)
            .build()
        val texture = Texture.builder()   // for face texture
            .setSource(this,R.drawable.clown_face_mesh_texture)
            .build()
        CompletableFuture.allOf(modelRenderable,texture) // it will be called upon the models and textures are ready
            .thenAccept {
                faceRenderable = modelRenderable.get().apply {
                    isShadowCaster = false // if we set isShadowCaster to FALSE then otehr models won't get shadded by our face model
                    isShadowReceiver = false // if we set isShadowReceiver to FALSE then otehr models won't make our model apply shadow
                } // We are passing modelrenderable to faceRendrable variable when it's ready
                faceTexture = texture.get() // passing face texture to faceTexture variable on it's loaded
            }
            .exceptionally {  // throws exception when there is problem in loading our model
                Toast.makeText(this,"Error loading mode : $it",Toast.LENGTH_LONG).show()
                null
            }
    }

}
